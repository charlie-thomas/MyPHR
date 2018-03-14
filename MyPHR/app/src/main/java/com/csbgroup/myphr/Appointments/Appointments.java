package com.csbgroup.myphr.Appointments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.csbgroup.myphr.AlarmReceiver;
import com.csbgroup.myphr.Calendar.CalendarEvent;
import com.csbgroup.myphr.Adapters.DateAdapter;
import com.csbgroup.myphr.MainActivity;
import com.csbgroup.myphr.R;
import com.csbgroup.myphr.database.AppDatabase;
import com.csbgroup.myphr.database.AppointmentsEntity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Appointments extends Fragment {

    private static Object mContext;

    // format error checking booleans
    private boolean validTitle = false;
    private boolean validDate = false;
    private boolean validTime = false;

    public Appointments() {} // Required empty public constructor

    public static Appointments newInstance() {
        return new Appointments();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mContext = this.getContext();

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

                ((MainActivity) getActivity()).switchFragment(details, true);
            }
        });

        // display no appointments message when contacts empty
        LinearLayout noapps = rootView.findViewById(R.id.no_apps);
        noapps.setVisibility(View.INVISIBLE);
        if (listView.getAdapter().getCount() == 0) noapps.setVisibility(View.VISIBLE);

        // fab action for adding appointment
        FloatingActionButton fab = rootView.findViewById(R.id.app_fab);
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
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Convert into CalendarEvent objects
        ArrayList<CalendarEvent> events = new ArrayList<>();

        if (appointments != null) {
            for (AppointmentsEntity ae : appointments)
                events.add(new CalendarEvent(ae.getUid(), 0, ae.getTime(), ae.getDate(), ae.getTitle() ,null));
        }

        Collections.sort(events, new Comparator<CalendarEvent>() {
            @Override
            public int compare(CalendarEvent e1, CalendarEvent e2) {
                @SuppressLint("SimpleDateFormat") DateFormat f = new SimpleDateFormat("dd/MM/yyyy");

                int dateComp = 0;
                try {
                    dateComp = f.parse(e2.getDate()).compareTo(f.parse(e1.getDate()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (dateComp != 0) {return dateComp;}

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
     * buildDialog builds the pop-up dialog for adding a new appointment, with input format checking.
     * @param fab the floating action button which pulls up the dialog
     */
    public void buildDialog(FloatingActionButton fab) {

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // set up the dialog
                LayoutInflater inflater = getActivity().getLayoutInflater(); // get inflater
                @SuppressLint("InflateParams") View v = inflater.inflate(R.layout.add_appointment_dialog, null);
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
                                ((MainActivity)getActivity()).switchFragment(newdetails, true);
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
                else {validDate = false; date_error.setError("Invalid date (DD MM YYYY)");} // invalid date

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
                else {validDate = false; date_error.setError("Invalid date (DD MM YYYY)");} // invalid date

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
                else {validDate = false; date_error.setError("Invalid date (DD MM YYYY)");} // invalid date

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
     * @return whether the input is a valid date
     */
    public boolean checkFullDate(EditText et1, EditText et2, EditText et3){

        boolean validDate = true;

        // join date into one string
        final String date = et1.getText().toString() + "/" + et2.getText().toString() + "/" + et3.getText().toString();

        if (date.equals("//")) {validDate = false;} // no date given
        else if (date.length() != 10) {validDate = false;} // incomplete date
        else {
            try {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date d = sdf.parse(date);
                if (!date.equals(sdf.format(d))){
                    validDate = false; // invalid date
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
     * @return whether the input was a valid i
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


    /**
     * Returns the current app context for use
     * in send/cancel notification functions, which are static
     */
    public static Context getAppContext() {
        return (Context)mContext;
    }


    /**
     * sendNotification runs every time the user changes anything in the reminders section of
     * an individual appointment. It gets the information submitted by the user about the appointment -
     * time, location, date, etc., and sends it to the notification creator, then uses the AlarmManager
     * to schedule it at the appropriate time to remind them.
     */
    public static void sendNotification(AppointmentsEntity appointment) {

        final Context mContext = getAppContext();

        String time = appointment.getTime();
        String date = appointment.getDate();

        final String name = appointment.getTitle();
        final String location = appointment.getLocation();

        if (appointment.getReminders()) {

            // Time variables
            int hourToSet = Integer.parseInt(time.substring(0,2));
            int minuteToSet = Integer.parseInt(time.substring(3,5));

            // Date variables
            int yearToSet = Integer.parseInt(date.substring(6,10));
            int monthToSet = Integer.parseInt(date.substring(3,5));
            int dayToSet = Integer.parseInt(date.substring(0,2));

            AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

            Intent intentAlarm = new Intent(mContext, AlarmReceiver.class);
            // Send the name of the medicine and whether notification should be descriptive to AlarmReceiver
            intentAlarm.putExtra("type", "appointment");
            intentAlarm.putExtra("location", location);
            intentAlarm.putExtra("appointment", name);
            intentAlarm.putExtra("descriptive", appointment.getReminder_type());
            intentAlarm.putExtra("time", time.substring(0,5));
            intentAlarm.putExtra("date", date.substring(0,5));

            // Intent variables
            PendingIntent notifyWeek = PendingIntent.getBroadcast(mContext, appointment.getUid()+1000, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent notifyDay = PendingIntent.getBroadcast(mContext, appointment.getUid()+2000, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent notifyMorning = PendingIntent.getBroadcast(mContext, appointment.getUid()+3000, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);

            // Set notification to launch at medicine reminder time
            Calendar calendar = Calendar.getInstance();
            Calendar timeNow = Calendar.getInstance();
            calendar.set(yearToSet, monthToSet, dayToSet, hourToSet, minuteToSet, 0);
            // Subtract one from month to account for Java calendar
            calendar.add(Calendar.MONTH, -1);

            // Clone calendar so each reminder can adjust it
            Calendar weekCalendar = (Calendar) calendar.clone();
            Calendar dayCalendar = (Calendar) calendar.clone();
            Calendar morningCalendar = (Calendar) calendar.clone();

            if (appointment.isRemind_week()) {
                // Subtract a week from calendar for prior week reminder
                weekCalendar.add(Calendar.DAY_OF_YEAR, -7);
                // Checks if event is in the past. If so, does not activate
                if (weekCalendar.compareTo(timeNow) == 1) {
                    // Android 6.0+ has Doze, which will silence alarms, so allow while idle is needed for that
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, weekCalendar.getTimeInMillis(), notifyWeek);
                    } else {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, weekCalendar.getTimeInMillis(), notifyWeek);
                    }
                }
            }

            if (appointment.isRemind_day()) {
                // Subtract a day from calendar for prior day reminder
                dayCalendar.add(Calendar.DAY_OF_YEAR, -1);
                // Checks if event is in the past. If so, does not activate
                if (dayCalendar.compareTo(timeNow) == 1) {
                    // Android 6.0+ has Doze, which will silence alarms, so allow while idle is needed for that
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, dayCalendar.getTimeInMillis(), notifyDay);
                    } else {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, dayCalendar.getTimeInMillis(), notifyWeek);
                    }
                }
            }

            if (appointment.isRemind_morning()) {
                // Set time for 10AM for same-day appointments
                morningCalendar.set(Calendar.HOUR_OF_DAY, 10);
                // Checks if event is in the past. If so, does not activate
                if (morningCalendar.compareTo(timeNow) == 1) {
                    // Android 6.0+ has Doze, which will silence alarms, so allow while idle is needed for that
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, morningCalendar.getTimeInMillis(), notifyMorning);
                    } else {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, morningCalendar.getTimeInMillis(), notifyWeek);
                    }
                }
            }
        }
    }


    /**
     * cancelNotification is called when the user switches off reminders altogether or specifically requests only
     * to be reminded at certain times. It cancels all notifications that have already been scheduled by the AlarmManager
     * that are no longer required.
     */
    public static void cancelNotification(AppointmentsEntity appointment, int id) {
        Intent intent = new Intent(getAppContext(), AlarmReceiver.class);
        PendingIntent Intent = PendingIntent.getBroadcast(getAppContext(), appointment.getUid()+id, intent, 0);
        AlarmManager alarmManager = (AlarmManager)getAppContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(Intent);
    }


    /**
     * resetNotifications is called when the user reboots their phone to reset the notifications
     */
    public static void resetNotifications() {

        final Activity activity = (Activity)mContext;

        // Create a callable object for database transactions
        Callable callable = new Callable() {
            @Override
            public Object call() throws Exception {
                return AppDatabase.getAppDatabase(activity).appointmentsDao().getAll();
            }
        };

        // Get a Future object of all the appointment titles
        ExecutorService service = Executors.newFixedThreadPool(2);
        Future<List<AppointmentsEntity>> result = service.submit(callable);

        // Create a list of the appointment names
        List<AppointmentsEntity> appointments = null;
        try {
            appointments = result.get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (appointments != null) {
            for (AppointmentsEntity ae : appointments)
                sendNotification(ae);
        }
    }
}
