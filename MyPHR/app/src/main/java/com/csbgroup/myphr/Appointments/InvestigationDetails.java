package com.csbgroup.myphr.Appointments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.csbgroup.myphr.MainActivity;
import com.csbgroup.myphr.R;
import com.csbgroup.myphr.database.AppDatabase;
import com.csbgroup.myphr.database.InvestigationsEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class InvestigationDetails extends Fragment {

    private InvestigationsEntity thisinvestigation; // the investigation we're viewing now

    private Menu editMenu;
    private String mode = "view";
    private View rootView;

    // key listeners and backgrounds for toggling field editability
    private KeyListener titlelistener, datelistener, noteslistener;
    private Drawable titlebackground, datebackground, notesbackground;

    // error checking booleans
    private Boolean validTitle = true;
    private Boolean validDate = true;

    public InvestigationDetails() {
        // Required empty public constructor
    }

    public static InvestigationDetails newInstance() {
        InvestigationDetails fragment = new InvestigationDetails();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_investigation_details, container, false);
        this.rootView = rootView;

        Bundle args = getArguments();
        InvestigationsEntity investigation = getInvestigation(Integer.parseInt(args.getString("uid")));
        thisinvestigation = investigation;

        EditText title = rootView.findViewById(R.id.investigation_name);
        EditText date = rootView.findViewById(R.id.investigation_date);
        EditText notes = rootView.findViewById(R.id.notes);

        // fill in the values
        title.setText(investigation.getTitle());
        date.setText(investigation.getDate());
        notes.setText(investigation.getNotes());

        // save listeners and backgrounds
        titlelistener = title.getKeyListener();
        titlebackground = title.getBackground();
        datelistener = date.getKeyListener();
        datebackground = date.getBackground();
        noteslistener = notes.getKeyListener();
        notesbackground = notes.getBackground();

        // disable editability
        disableEditing(title);
        disableEditing(date);
        disableEditing(notes);

        // back button
        ((MainActivity) getActivity()).setToolbar("", true);
        setHasOptionsMenu(true);

        return rootView;
    }

    /**
     * Fetches a single investigation from the database.
     * @param uid is the primary key of the investigation to be retrieved
     * @return the investigation entity
     */
    private InvestigationsEntity getInvestigation(final int uid) {

        // Create a callable object for database transactions
        Callable callable = new Callable() {
            @Override
            public Object call() throws Exception {
                return AppDatabase.getAppDatabase(getActivity()).investigationDao().getInvestigation(uid);
            }
        };

        ExecutorService service = Executors.newFixedThreadPool(2);
        Future<InvestigationsEntity> result = service.submit(callable);

        InvestigationsEntity investigation = null;
        try {
            investigation = result.get();
        } catch (Exception e) {}

        return investigation;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.edit, menu);
        editMenu = menu;
    }

    /**
     * Provides navigation/actions for menu items.
     * @param item is the clicked menu item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // back button - go back
                ((MainActivity) getActivity()).switchFragment(AppointmentsSection.newInstance(), false);
                return true;

            case R.id.details_edit: // edit button - edit appointment details
                switchMode();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * switchMode toggles between viewing and editing the appointment details.
     */
    public void switchMode() {

        final EditText title = rootView.findViewById(R.id.investigation_name);
        final EditText date = rootView.findViewById(R.id.investigation_date);
        final EditText notes = rootView.findViewById(R.id.notes);
        final Button delete = rootView.findViewById(R.id.delete);

        if (this.mode.equals("view")) { // entering edit mode
            editMenu.getItem(0).setIcon(R.drawable.tick);

            // activate error checking
            errorChecking(title, date);

            // show the delete button
            delete.setVisibility(View.VISIBLE);

            // restore bg an kl to make editable
            title.setBackground(titlebackground);
            title.setKeyListener(titlelistener);
            date.setKeyListener(datelistener);
            date.setBackground(datebackground);
            notes.setKeyListener(noteslistener);
            notes.setBackground(notesbackground);

            // confirm investigation deletion
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // set up the view
                    LayoutInflater inflater = getActivity().getLayoutInflater(); // get inflater
                    View v = inflater.inflate(R.layout.confirm_delete, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setView(v);
                    final TextView message = v.findViewById(R.id.message);
                    message.setText("Are you sure you want to delete " + thisinvestigation.getTitle() + "?");

                    // delete the appointment
                    builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    AppDatabase db = AppDatabase.getAppDatabase(getActivity());
                                    db.investigationDao().delete(thisinvestigation);
                                    ((MainActivity) getActivity()).switchFragment(AppointmentsSection.newInstance(), false);
                                }
                            }).start();
                        }
                    });

                    // cancel the delete
                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {}
                    });

                    final AlertDialog dialog = builder.create();

                    dialog.show();

                    // set button colours
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.colorRed));
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.colorRed));
                }
            });

            this.mode = "edit";
            return;
        }

        if (this.mode.equals("edit")){ // exiting edit mode
            editMenu.getItem(0).setIcon(R.drawable.edit);

            // hide the delete button
            delete.setVisibility(View.GONE);

            // disable editing of all fields
            disableEditing(title);
            disableEditing(date);
            disableEditing(notes);

            // update the investigation in the database
            new Thread(new Runnable() {
                @Override
                public void run() {
                    AppDatabase db = AppDatabase.getAppDatabase(getActivity());
                    thisinvestigation.setTitle(title.getText().toString());
                    thisinvestigation.setDate(date.getText().toString());
                    thisinvestigation.setNotes(notes.getText().toString());
                    db.investigationDao().update(thisinvestigation);

                    // refresh to get rid of keyboard
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.detach(InvestigationDetails.this).attach(InvestigationDetails.this).commit();
                }
            }).start();

            this.mode = "view";
        }
    }

    /**
     * disableEditing sets background and keylistener to null to stop user editing
     * @param field is the editText field to be disabled
     */
    public void disableEditing(EditText field){
        field.setBackground(null);
        field.setKeyListener(null);
    }

    /**
     * errorChecking live checks the formatting of fields; errors are highlighted to the user
     * and saving is disabled until they are corrected.
     * @param et1 is the investigation title, which cannot be empty
     * @param et2 is the investigation date, which must be a valid date
     */
    public void errorChecking(EditText et1, EditText et2){

        final EditText title = et1;
        final EditText date = et2;

        // title format checking
        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (title.getText().length() != 0){validTitle = true;} // valid title
                else {validTitle = false; title.setError("Title cannot be empty");} // empty title

                // disable/enable save button following format checks
                if (validTitle && validDate) {editMenu.getItem(0).setEnabled(true);}
                else {editMenu.getItem(0).setEnabled(false);}
            }

            // not needed for our purposes
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void afterTextChanged(Editable editable) {}
        });

        // date error checking
        date.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String d = date.getText().toString();
                if (d.length() != 10) {validDate = false; date.setError("Invalid date (DD/MM/YYYY");} // invalid format
                else {
                    try { // valid format
                        validDate = true;
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        if (!d.equals(sdf.format(sdf.parse(d)))) { // invalid value
                            validDate = false;
                            date.setError("Invalid date (DD/MM/YYYY)");
                        }
                        else {validDate = true;} // valid value
                    } catch (ParseException e) {e.printStackTrace();
                    }
                }

                // disable/enable save button following format checks
                if (validTitle && validDate) {editMenu.getItem(0).setEnabled(true);}
                else {editMenu.getItem(0).setEnabled(false);}
            }

            // not needed for our purposes
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void afterTextChanged(Editable editable) {}
        });
    }

}