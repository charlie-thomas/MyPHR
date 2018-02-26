package com.csbgroup.myphr;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.method.KeyListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.csbgroup.myphr.database.AppDatabase;
import com.csbgroup.myphr.database.InvestigationsEntity;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.zip.GZIPOutputStream;

public class InvestigationDetails extends Fragment {

    private InvestigationsEntity thisinvestigation; // the investigation we're viewing now

    private Menu editMenu;
    private String mode = "view";
    private View rootView;

    // key listeners and backgrounds for toggling field editability
    private KeyListener titlelistener, datelistener, noteslistener;
    private Drawable titlebackground, datebackground, notesbackground;

    // error checking booleans
    private Boolean validTitle;
    private Boolean validDate;

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
        ((MainActivity) getActivity()).setToolbar("My Appointments", true);
        setHasOptionsMenu(true);

        return rootView;
    }

    /**
     * Fetches a single appointment from the database.
     * @param uid is the primary key of the appointment to be retrieved
     * @return the appointment entity
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
                ((MainActivity) getActivity()).switchFragment(AppointmentsSection.newInstance());
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

            // show the delete button
            delete.setVisibility(View.VISIBLE);

            // restore bg an kl to make editable
            title.setBackground(titlebackground);
            title.setKeyListener(titlelistener);
            date.setKeyListener(datelistener);
            date.setBackground(datebackground);
            notes.setKeyListener(noteslistener);
            notes.setBackground(notesbackground);

            // delete the investigation
            delete.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            AppDatabase db = AppDatabase.getAppDatabase(getActivity());
                            db.investigationDao().delete(thisinvestigation);
                            ((MainActivity) getActivity()).switchFragment(Investigations.newInstance());
                        }
                    }).start();
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
            return;
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

}