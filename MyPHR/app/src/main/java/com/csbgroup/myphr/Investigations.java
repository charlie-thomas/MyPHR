package com.csbgroup.myphr;

import android.app.AlertDialog;
import android.support.v4.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.csbgroup.myphr.database.AppDatabase;
import com.csbgroup.myphr.database.InvestigationsEntity;
import com.csbgroup.myphr.database.MedicineEntity;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Investigations extends Fragment {

    private FloatingActionButton fab; // the add investigation fab

    // format error chekcing booleans
    private boolean validTitle  = false;
    private boolean validDate = false;

    public Investigations() {
        // Required empty public constructor
    }

    public static Investigations newInstance() {
        Investigations fragment = new Investigations();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // view set up
        View rootView = inflater.inflate(R.layout.fragment_investigations, container, false);

        // display the investigations in list
        DateAdapter adapter = new DateAdapter(getActivity(), getInvestigations());
        ListView listView = rootView.findViewById(R.id.investigations_list);
        listView.setAdapter(adapter);

        // switching to details fragment
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment details = InvestigationDetails.newInstance();

                // Create a bundle to pass the appointment to the details fragment
                Bundle bundle = new Bundle();
                bundle.putString("uid", view.getTag().toString());
                details.setArguments(bundle);

                ((MainActivity) getActivity()).switchFragment(details);
            }
        });

        // fab action for adding investigation
        fab = rootView.findViewById(R.id.investigation_fab);
        buildDialog(fab);

        return rootView;
    }

    /**
     * getInvestigations fetches the list of investigations from the database
     * @return the list of investigations
     */
    public List<CalendarEvent> getInvestigations() {

        // Create a callable object for database transactions
        Callable callable = new Callable() {
            @Override
            public Object call() throws Exception {
                return AppDatabase.getAppDatabase(getActivity()).investigationDao().getAll();
            }
        };

        // Get a Future object of all the investigation titles
        ExecutorService service = Executors.newFixedThreadPool(2);
        Future<List<InvestigationsEntity>> result = service.submit(callable);

        // Create a list of the investigation titles
        List<InvestigationsEntity> investigations = null;
        try {
            investigations = result.get();
        } catch (Exception e) {}


        // Convert into CalendarEvent objects
        List<CalendarEvent> events = new ArrayList<>();

        if (investigations != null) {
            for (InvestigationsEntity ie : investigations)events.add(new CalendarEvent(ie.getUid(), 0, null,  ie.getDate(), ie.getTitle() ,null));
        }

        Collections.sort(events, new Comparator<CalendarEvent>() {
            @Override
            public int compare(CalendarEvent e1, CalendarEvent e2) {

                DateFormat f = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    return f.parse(e2.getDate()).compareTo(f.parse(e1.getDate()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });

        return events;
    }

    /**
     * buildDialog builds the pop-up dialog for adding a new investigation
     * @param fab the floating action button which pulls up the dialog
     */
    public void buildDialog(FloatingActionButton fab) {

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // set up the dialog
                LayoutInflater inflater = getActivity().getLayoutInflater(); // get inflater
                View v = inflater.inflate(R.layout.add_investigation_dialog, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setView(v);

                // fetch the input values
                final EditText title = v.findViewById(R.id.inv_title);
                final EditText day = v.findViewById(R.id.inv_DD);
                final EditText month = v.findViewById(R.id.inv_MM);
                final EditText year = v.findViewById(R.id.inv_YYYY);
                final EditText notes = v.findViewById(R.id.inv_notes);

                // hide the invisible edit text
                EditText date_error = v.findViewById(R.id.date_error);
                date_error.setKeyListener(null);
                date_error.setBackground(null);

                // add a new investigation action
                builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        // join date into one string
                        final String date = day.getText().toString() + "/" + month.getText().toString()
                                + "/" + year.getText().toString();

                        // check that a title has been given
                        Boolean validTitle = true;
                        if (title.getText().toString().equals("")){
                            validTitle = false;
                        }

                        new Thread(new Runnable() {
                                @Override
                                public void run() {
                                AppDatabase db = AppDatabase.getAppDatabase(getActivity());
                                InvestigationsEntity investigation = new InvestigationsEntity(
                                        title.getText().toString(), date, notes.getText().toString());
                                long uid = db.investigationDao().insert(investigation);

                                // Move to details fragment for new appointment
                                Fragment newdetails = InvestigationDetails.newInstance();
                                Bundle bundle = new Bundle();
                                bundle.putString("uid", String.valueOf(uid));
                                newdetails.setArguments(bundle);
                                ((MainActivity)getActivity()).switchFragment(newdetails);

                                }
                            }).start();
                    }

                });

                // action for cancelling activity
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int arg1) {
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

                // disable the add button until input conditions are met
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

                // check user input
                inputChecking(v, dialog);
            }
        });

    }

    /**
     * inputChecking checks the user input when adding a new investigation, the add button is disabled
     * until all format conditions are met.
     * @param v is the view for the add investigation dialog.
     * @param ad is the new investigation alertdog.
     */
    public void inputChecking(View v, AlertDialog ad){

        final EditText title = v.findViewById(R.id.inv_title);
        final EditText day = v.findViewById(R.id.inv_DD);
        final EditText month = v.findViewById(R.id.inv_MM);
        final EditText year = v.findViewById(R.id.inv_YYYY);
        final EditText notes = v.findViewById(R.id.inv_notes);
        final EditText date_error = v.findViewById(R.id.date_error);
        final AlertDialog dialog = ad;

        // ensure investigation title is valid
        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (title.getText().length() != 0){validTitle = true;} // valid title
                else {validTitle = false; title.setError("Title cannot be empty");} // empty title

                // disable/enable add button following format checks
                if (validTitle && validDate) {dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);}
                else {dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);}
            }
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void afterTextChanged(Editable editable) {}
        });

        // ensure appointment day is valid
        day.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (checkFullDate(day, month, year)){validDate = true; date_error.setError(null);} // valid date
                else {validDate = false; date_error.setError("Invalid date (DD MM YYYY");} // invalid date

                // disable/enable add button following format checks
                if (validTitle && validDate) {dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);}
                else {dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);}

                if (day.getText().toString().length() == 2) {month.requestFocus();}
            }
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void afterTextChanged(Editable editable) {}
        });

        // ensure appointment month is valid
        month.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (checkFullDate(day, month, year)){validDate = true; date_error.setError(null);} // valid date
                else {validDate = false; date_error.setError("Invalid date (DD MM YYYY");} // invalid date

                // disable/enable add button following format checks
                if (validTitle && validDate) {dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);}
                else {dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);}

                if (month.getText().toString().length() == 2) {year.requestFocus();}
            }
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void afterTextChanged(Editable editable) {}
        });

        // ensure appointment year is valid
        year.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (checkFullDate(day, month, year)){validDate = true; date_error.setError(null);} // valid date
                else {validDate = false; date_error.setError("Invalid date (DD MM YYYY");} // invalid date

                // disable/enable add button following format checks
                if (validTitle && validDate) {dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);}
                else {dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);}

                if (year.getText().toString().length() == 4) {notes.requestFocus();}
            }
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void afterTextChanged(Editable editable) {}
        });
    }

    /**
     * checkFullDate checks the validity of the full date across the three fields in the add dialog
     * whenever any of them is changed.
     * @param et1 is the day.
     * @param et2 is the month.
     * @param et3 is the year.
     * @return
     */
    public boolean checkFullDate(EditText et1, EditText et2, EditText et3){

        boolean validDate = true;

        // join date into one string
        final String date = et1.getText().toString() + "/" + et2.getText().toString() + "/" + et3.getText().toString();

        if (date.equals("//")) {validDate = false;} // no date given
        else if (date.length() != 10) {validDate = false;} // incomplete date
        else {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date d = sdf.parse(date);
                if (!date.equals(sdf.format(d))){
                    validDate = false;
                }
            } catch (ParseException e) {e.printStackTrace();}
        }

        return validDate;
    }
}