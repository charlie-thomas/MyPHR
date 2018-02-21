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
import android.widget.TextView;

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
     * Provides navigation for menu items; currently only needed for navigation to settings
     * fragment.
     * @param item is the clicked menu item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            ((MainActivity) getActivity()).switchFragment(AppointmentsSettings.newInstance());
            return true;
        }
        return super.onOptionsItemSelected(item);
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

                // auto shift view focus when entering date
                shiftFocus(day, month, year, hour, mins, notes);

                // add new appointment action
                builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        // join date into one string
                        final String date = day.getText().toString() + "/" + month.getText().toString()
                                + "/" + year.getText().toString();

                        // join time into one string
                        final String time = hour.getText().toString() + ":" + mins.getText().toString();

                        // check that a title has been given
                        Boolean validTitle = true;
                        if (title.getText().toString().equals("")){
                            validTitle = false;
                        }

                        // check that a valid date was given
                        Boolean validDate = true;
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

                        // check that a valid time was given
                        Boolean validTime = true;
                        if (time.equals(":")) {validTime = false;}// no time given
                        else{
                            int hourint = Integer.parseInt(hour.getText().toString()); // convert to int for checks
                            int minsint = Integer.parseInt(mins.getText().toString()); // convert to int for checks
                            if (hourint <0 || hourint >23) {validTime = false;}
                            if (minsint <0 || minsint >59) {validTime = false;}
                        }


                        // format checks passed - add the new appointment to the database
                        if (validTitle && validDate && validTime){
                            new Thread(new Runnable(){
                                @Override
                                public void run(){
                                    AppDatabase db = AppDatabase.getAppDatabase(getActivity());
                                    AppointmentsEntity appointment = new AppointmentsEntity(
                                            title.getText().toString(), location.getText().toString(),
                                            date, time, notes.getText().toString(), false);
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

                        // format checks failed - abort and show error message
                        else {
                            if (!validTitle){errorDialog("title");} // no title
                            else if (!validDate){errorDialog("date");} // bad date
                            else {errorDialog("time");} // bad time
                        }
                    }
                });

                // action for cancelling add
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    /**
     * errorDialog is called when an invalid title, date or time
     * is part of an appointment being added, it displays an error message about the failure.
     * @param type is the type of error reported
     */
    public void errorDialog(String type){

        // set up the dialog
        LayoutInflater inflater = getActivity().getLayoutInflater(); // get inflater
        View v = inflater.inflate(R.layout.format_error, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);

        // specify error type
        final TextView errortype = v.findViewById(R.id.error_type);
        if (type.equals("title")){errortype.setText("YOU MUST PROVIDE A TITLE");}
        if (type.equals("date")){errortype.setText("INVALID DATE");}
        if (type.equals("time")){errortype.setText("INVALID TIME");}

        final TextView errormessage = v.findViewById(R.id.error_message);
        errormessage.setText("Your appointment was not added.");

        // user dismiss message
        builder.setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * shiftFocus automatically shifts the fab dialog view focus from day->month and month->year
     * when two digits have been entered for day and month, respectively.
     * @param day is the EditText for the dialog day('DD') field
     * @param month is the EditText for the dialog month('MM') field
     * @param year is the EditText for the dialog year('YYYY') field
     * @param hour is the EditText for the dialog hour field
     * @param mins is the EditText for the dialog mins field
     * @param next is the EditText for the dialog field that follows mins
     */
    public void shiftFocus(final EditText day, final EditText month, final EditText year,
                           final EditText hour, final EditText mins, final EditText next){

        day.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (day.getText().toString().length() == 2) {month.requestFocus();}
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        month.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (month.getText().toString().length() == 2) {year.requestFocus();}
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        year.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (year.getText().toString().length() == 4) {hour.requestFocus();}
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        hour.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (hour.getText().toString().length() == 2) {mins.requestFocus();}
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        mins.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mins.getText().toString().length() == 2) {next.requestFocus();}
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });


    }

}
