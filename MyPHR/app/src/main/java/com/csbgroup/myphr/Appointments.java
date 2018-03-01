package com.csbgroup.myphr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.csbgroup.myphr.database.AppDatabase;
import com.csbgroup.myphr.database.AppointmentsEntity;

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

public class Appointments extends Fragment {

    private FloatingActionButton fab; // the add appointment fab

    // format error checking booleans
    private boolean validTitle = false;
    private boolean validDate = false;
    private boolean validTime = false;

    public Appointments() {
        // Required empty public constructor
    }

    public static Appointments newInstance() {
        Appointments fragment = new Appointments();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // view set up
        View rootView = inflater.inflate(R.layout.fragment_appointments, container, false);
        ((MainActivity) getActivity()).setToolbar("My Appointments", false);
        setHasOptionsMenu(true);

        // display the appointments in list
        DateAdapter appointmentsAdapter = new DateAdapter(getActivity(), getAppointments());
        ListView listView = rootView.findViewById(R.id.appointments_list);
        listView.setAdapter(appointmentsAdapter);

        // switching to details fragment
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment details = AppointmentsDetails.newInstance();

                // Create a bundle to pass the appointment to the details fragment
                Bundle bundle = new Bundle();
                bundle.putString("uid", view.getTag().toString());
                details.setArguments(bundle);

                ((MainActivity) getActivity()).switchFragment(details);
            }
        });

        // fab action for adding appointment
        fab = rootView.findViewById(R.id.app_fab);
        buildDialog(fab);

        return rootView;
    }

    /**
     * getAppointments fetches the list of appointments from the database
     * @return the list of appointment entities
     */
    private List<CalendarEvent> getAppointments() {

        // Create a callable object for database transactions
        Callable callable = new Callable() {
            @Override
            public Object call() throws Exception {
                return AppDatabase.getAppDatabase(getActivity()).appointmentsDao().getAll();
            }
        };

        // Get a Future object of all the appointment titles
        ExecutorService service = Executors.newFixedThreadPool(2);
        Future<List<AppointmentsEntity>> result = service.submit(callable);

        // Create a list of the appointment names
        List<AppointmentsEntity> appointments = null;
        try {
            appointments = result.get();
        } catch (Exception e) {}

        // Convert into CalendarEvent objects
        ArrayList<CalendarEvent> events = new ArrayList<>();

        if (appointments != null) {
            for (AppointmentsEntity ae : appointments)
                events.add(new CalendarEvent(ae.getUid(), 0, ae.getTime(), ae.getDate(), ae.getTitle() ,null));
        }

        Collections.sort(events, new Comparator<CalendarEvent>() {
            @Override
            public int compare(CalendarEvent e1, CalendarEvent e2) {

                DateFormat f = new SimpleDateFormat("dd/MM/yyyy");

                int dateComp = 0;
                try {
                    dateComp = f.parse(e2.getDate()).compareTo(f.parse(e1.getDate()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (dateComp != 0) return dateComp;

                return e2.getTime().replace(":", "").compareTo(e1.getTime().replace(":", ""));
            }
        });

        return events;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * buildDialog builds the pop-up dialog for adding a new appointment
     * @param fab the floating action button which pulls up the dialog
     */
    public void buildDialog(FloatingActionButton fab) {

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // set up the dialog
                LayoutInflater inflater = getActivity().getLayoutInflater(); // get inflater
                View v = inflater.inflate(R.layout.add_appointment_dialog, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setView(v);

                // fetch the input values
                final EditText title = v.findViewById(R.id.appointment_name);
                final EditText location = v.findViewById(R.id.appointment_location);
                final EditText day = v.findViewById(R.id.appointment_DD);
                final EditText month = v.findViewById(R.id.appointment_MM);
                final EditText year = v.findViewById(R.id.appointment_YYYY);
                final EditText hour = v.findViewById(R.id.appointment_hour);
                final EditText mins = v.findViewById(R.id.appointment_min);
                final EditText notes = v.findViewById(R.id.appointment_notes);

                // hide the invisible edittexts
                EditText date_error = v.findViewById(R.id.date_error);
                date_error.setKeyListener(null);
                date_error.setBackground(null);
                EditText time_error = v.findViewById(R.id.time_error);
                time_error.setKeyListener(null);
                time_error.setBackground(null);

                // add new appointment action
                builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        // join date into one string
                        final String date = day.getText().toString() + "/" + month.getText().toString()
                                + "/" + year.getText().toString();

                        // join time into one string
                        final String time = hour.getText().toString() + ":" + mins.getText().toString();

                        // Add the new appointment to the database
                        new Thread(new Runnable(){
                                @Override
                                public void run(){
                                AppDatabase db = AppDatabase.getAppDatabase(getActivity());
                                AppointmentsEntity appointment = new AppointmentsEntity(
                                        title.getText().toString(), location.getText().toString(),
                                        date, time, notes.getText().toString(), false, 0,
                                        false, false, false);
                                long uid = db.appointmentsDao().insert(appointment);

                                // Move to details fragment for new appointment
                                Fragment newdetails = AppointmentsDetails.newInstance();
                                Bundle bundle = new Bundle();
                                bundle.putString("uid", String.valueOf(uid));
                                newdetails.setArguments(bundle);
                                ((MainActivity)getActivity()).switchFragment(newdetails);
                                }
                        }).start();
                    }
                });

                // cancel the add
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {}
                });

                final AlertDialog dialog = builder.create();
                dialog.show();

                // disable the add button until input conditions are met
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

                // check user input
                inputChecking(v, dialog);
            }
        });
    }

    /**
     * inputChecking checks the user input when adding a new appointment, the add button is disabled
     * until all format conditions are met.
     * @param v is the view for the add appointment dialog.
     * @param ad is the new contact alertdialog.
     */
    public void inputChecking(View v, AlertDialog ad){

        final EditText title = v.findViewById(R.id.appointment_name);
        final EditText day = v.findViewById(R.id.appointment_DD);
        final EditText month = v.findViewById(R.id.appointment_MM);
        final EditText year = v.findViewById(R.id.appointment_YYYY);
        final EditText hour = v.findViewById(R.id.appointment_hour);
        final EditText mins = v.findViewById(R.id.appointment_min);
        final EditText notes = v.findViewById(R.id.appointment_notes);
        final EditText date_error = v.findViewById(R.id.date_error);
        final EditText time_error = v.findViewById(R.id.time_error);
        final AlertDialog dialog = ad;

        // ensure appointment title is present
        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (title.getText().length() != 0){validTitle = true;} // valid title
                else {validTitle = false; title.setError("Title cannot be empty");} // empty title

                // disable/enable add button following format checks
                if (validTitle && validTime && validDate) {dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);}
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
                if (validTitle && validTime && validDate) {dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);}
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
                if (validTitle && validTime && validDate) {dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);}
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
                if (validTitle && validTime && validDate) {dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);}
                else {dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);}

                if (year.getText().toString().length() == 4) {hour.requestFocus();}
            }
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void afterTextChanged(Editable editable) {}
        });

        // ensure appointment hour is valid
        hour.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (checkFullTime(hour, mins)){validTime = true; time_error.setError(null);} // valid date
                else {validTime = false; time_error.setError("Invalid time (HH MM)");} // invalid date

                // disable/enable add button following format checks
                if (validTitle && validTime && validDate) {dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);}
                else {dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);}

                if (hour.getText().toString().length() == 2) {mins.requestFocus();}
            }
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void afterTextChanged(Editable editable) {}
        });

        // ensure the appointment minutes is valid
        mins.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (checkFullTime(hour, mins)){validTime = true; time_error.setError(null);} // valid date
                else {validTime = false; time_error.setError("Invalid time (HH MM)");} // invalid date

                // disable/enable add button following format checks
                if (validTitle && validTime && validDate) {dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);}
                else {dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);}

                if (mins.getText().toString().length() == 2) {notes.requestFocus();}

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

    /**
     * checkFullTime checks the validity of the full time across the two time fields in the add dialog
     * whenever either of them is changed.
     * @param et1 is the hour.
     * @param et2 is the minutes.
     */
    public boolean checkFullTime(EditText et1, EditText et2){

        boolean validTime = true;

        // join time into one string
        final String time = et1.getText().toString() + ":" + et2.getText().toString();

        if (time.length() != 5) {validTime = false;}// no time given
        else{
            int hourint = Integer.parseInt(et1.getText().toString()); // convert to int for checks
            int minsint = Integer.parseInt(et2.getText().toString()); // convert to int for checks
            if (hourint <0 || hourint >23) {validTime = false;}
            if (minsint <0 || minsint >59) {validTime = false;}
        }

        return validTime;
    }

}
