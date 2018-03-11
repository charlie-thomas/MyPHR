package com.csbgroup.myphr.Appointments;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.csbgroup.myphr.AlarmReceiver;
import com.csbgroup.myphr.MainActivity;
import com.csbgroup.myphr.R;
import com.csbgroup.myphr.database.AppDatabase;
import com.csbgroup.myphr.database.AppointmentsEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AppointmentsDetails extends Fragment {

    private AppointmentsEntity thisappointment; // the appointment we're viewing now

    private Menu editMenu;
    private String mode = "view";
    private View rootView;

    // key listeners and backgrounds for toggling field editability
    private KeyListener titleKL, locationKL, dateKL, timeKL, notesKL;
    private Drawable titleBG, locationBG, dateBG, timeBG, notesBG;

    // format error checking booleans
    private Boolean validTitle = true;
    private Boolean validTime = true;
    private Boolean validDate = true;

    public AppointmentsDetails() {
        // Required empty public constructor
    }

    public static AppointmentsDetails newInstance() {
        AppointmentsDetails fragment = new AppointmentsDetails();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_appointments_details, container, false);
        this.rootView = rootView;
        
        Bundle args = getArguments();
        AppointmentsEntity appointment = getAppointment(Integer.parseInt(args.getString("uid")));
        thisappointment = appointment;

        EditText title = rootView.findViewById(R.id.appointments_title);
        EditText location = rootView.findViewById(R.id.app_location);
        EditText date = rootView.findViewById(R.id.app_date);
        EditText time = rootView.findViewById(R.id.app_time);
        EditText notes = rootView.findViewById(R.id.app_notes);

        //fill in the values
        title.setText(appointment.getTitle());
        location.setText(appointment.getLocation());
        date.setText(appointment.getDate());
        time.setText(appointment.getTime());
        notes.setText(appointment.getNotes());

        // save listeners and backgrounds
        titleKL = title.getKeyListener();
        titleBG = title.getBackground();
        locationKL = location.getKeyListener();
        locationBG = location.getBackground();
        dateKL = date.getKeyListener();
        dateBG = date.getBackground();
        timeKL = time.getKeyListener();
        timeBG = time.getBackground();
        notesKL = notes.getKeyListener();
        notesBG = notes.getBackground();

        //disable editability
        disableEditing(title);
        disableEditing(location);
        disableEditing(date);
        disableEditing(time);
        disableEditing(notes);

        Switch reminders = rootView.findViewById(R.id.reminder_switch);
        reminders.setChecked(appointment.getReminders());

        RadioButton general = rootView.findViewById(R.id.general);
        RadioButton descriptive = rootView.findViewById(R.id.descriptive);
        CheckBox week = rootView.findViewById(R.id.checkBox1);
        CheckBox day = rootView.findViewById(R.id.checkBox2);
        CheckBox morning = rootView.findViewById(R.id.checkBox3);

        // check general/descriptive radios to reflect database
        if (thisappointment.getReminder_type() == 0){
            descriptive.setChecked(false);
            general.setChecked(true);
        }
        else {
            general.setChecked(false);
            descriptive.setChecked(true);
        }

        // check week/day/morning checkboxes to reflect database
        if (thisappointment.isRemind_week()){week.setChecked(true);}
        if (thisappointment.isRemind_day()){day.setChecked(true);}
        if (thisappointment.isRemind_morning()){morning.setChecked(true);}


        // update the database when general/descriptive radio buttons are changed
        RadioGroup radioGroup = rootView.findViewById(R.id.radios);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.general:
                        thisappointment.setReminder_type(0);
                        break;
                    case R.id.descriptive:
                        thisappointment.setReminder_type(1);
                        break;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        AppDatabase db = AppDatabase.getAppDatabase(getActivity());
                        db.appointmentsDao().update(thisappointment);
                    }
                }).start();
            }
        });

        // update the database when week/day/morning checkboxes are changed
        week.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (isChecked) {
                    sendNotification();
                    thisappointment.setRemind_week(true);
                }
                else {
                    cancelNotification();
                    thisappointment.setRemind_week(false);
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        AppDatabase db = AppDatabase.getAppDatabase(getActivity());
                        db.appointmentsDao().update(thisappointment);
                    }
                }).start();
            }
        });
        day.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (isChecked) {
                    thisappointment.setRemind_day(true);
                    sendNotification();
                }
                else {thisappointment.setRemind_day(false);}

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        AppDatabase db = AppDatabase.getAppDatabase(getActivity());
                        db.appointmentsDao().update(thisappointment);
                    }
                }).start();
            }
        });
        morning.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (isChecked) {
                    thisappointment.setRemind_morning(true);
                    sendNotification();
                }
                else {thisappointment.setRemind_morning(false);}

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        AppDatabase db = AppDatabase.getAppDatabase(getActivity());
                        db.appointmentsDao().update(thisappointment);
                    }
                }).start();
            }
        });

        // show the reminders options if reminders are on
        if (appointment.getReminders()){
            general.setVisibility(View.VISIBLE);
            descriptive.setVisibility(View.VISIBLE);
            week.setVisibility(View.VISIBLE);
            day.setVisibility(View.VISIBLE);
            morning.setVisibility(View.VISIBLE);
        }

        // hide/show reminders options as switch is toggled
        reminders.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        AppDatabase db = AppDatabase.getAppDatabase(getActivity());
                        thisappointment.setReminders(isChecked);
                        db.appointmentsDao().update(thisappointment);
                    }
                }).start();

                RadioButton general = rootView.findViewById(R.id.general);
                RadioButton descriptive = rootView.findViewById(R.id.descriptive);
                CheckBox week = rootView.findViewById(R.id.checkBox1);
                CheckBox day = rootView.findViewById(R.id.checkBox2);
                CheckBox morning = rootView.findViewById(R.id.checkBox3);

                if (isChecked) { // reminders are on
                    sendNotification();

                    general.setVisibility(View.VISIBLE);
                    descriptive.setVisibility(View.VISIBLE);
                    week.setVisibility(View.VISIBLE);
                    day.setVisibility(View.VISIBLE);
                    morning.setVisibility(View.VISIBLE);
                }
                else { // reminders are off
                    cancelNotification();

                    general.setVisibility(View.GONE);
                    descriptive.setVisibility(View.GONE);
                    week.setVisibility(View.GONE);
                    day.setVisibility(View.GONE);
                    morning.setVisibility(View.GONE);
                }
            }
        });

        // back button
        ((MainActivity) getActivity()).setToolbar("", true);
        setHasOptionsMenu(true);

        return rootView;
    }

    /**
     * Fetches a single appointment from the database.
     * @param uid is the primary key of the appointment to be retrieved
     * @return the appointment entity
     */
    private AppointmentsEntity getAppointment(final int uid) {

        // Create a callable object for database transactions
        Callable callable = new Callable() {
            @Override
            public Object call() throws Exception {
                return AppDatabase.getAppDatabase(getActivity()).appointmentsDao().getAppointment(uid);
            }
        };

        ExecutorService service = Executors.newFixedThreadPool(2);
        Future<AppointmentsEntity> result = service.submit(callable);

        AppointmentsEntity appointment = null;
        try {
            appointment = result.get();
        } catch (Exception e) {}

        return appointment;

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
                BottomNavigationView bn = getActivity().findViewById(R.id.bottom_nav);
                bn.setSelectedItemId(R.id.appointments);
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

        final EditText title = rootView.findViewById(R.id.appointments_title);
        final EditText location = rootView.findViewById(R.id.app_location);
        final EditText date = rootView.findViewById(R.id.app_date);
        final EditText time = rootView.findViewById(R.id.app_time);
        final EditText notes = rootView.findViewById(R.id.app_notes);
        final Button delete = rootView.findViewById(R.id.delete);

        if (this.mode.equals("view")) {
            editMenu.getItem(0).setIcon(R.drawable.tick);

            // activate error checking
            errorChecking(title, time, date);

            // show the delete button
            delete.setVisibility(View.VISIBLE);

            // restore bg and kl to make editable
            title.setBackground(titleBG);
            title.setKeyListener(titleKL);
            location.setKeyListener(locationKL);
            location.setBackground(locationBG);
            date.setKeyListener(dateKL);
            date.setBackground(dateBG);
            time.setKeyListener(timeKL);
            time.setBackground(timeBG);
            notes.setKeyListener(notesKL);
            notes.setBackground(notesBG);

            // confirm appointment deletion
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // set up the view
                    LayoutInflater inflater = getActivity().getLayoutInflater(); // get inflater
                    View v = inflater.inflate(R.layout.confirm_delete, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setView(v);
                    final TextView message = v.findViewById(R.id.message);
                    message.setText("Are you sure you want to delete " + thisappointment.getTitle() + "?");

                    // delete the appointment
                    builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    AppDatabase db = AppDatabase.getAppDatabase(getActivity());
                                    db.appointmentsDao().delete(thisappointment);
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

        if (this.mode.equals("edit")){
            sendNotification();

            editMenu.getItem(0).setIcon(R.drawable.edit);

            // hide the delete button
            delete.setVisibility(View.GONE);

            // disable editing of all fields
            disableEditing(title);
            disableEditing(location);
            disableEditing(date);
            disableEditing(time);
            disableEditing(notes);

            // update the contact in the database
            new Thread(new Runnable() {
                @Override
                public void run() {
                    AppDatabase db = AppDatabase.getAppDatabase(getActivity());
                    thisappointment.setTitle(title.getText().toString());
                    thisappointment.setLocation(location.getText().toString());
                    thisappointment.setTime(time.getText().toString());
                    thisappointment.setDate(date.getText().toString());
                    thisappointment.setNotes(notes.getText().toString());
                    db.appointmentsDao().update(thisappointment);

                    // refresh to get rid of keyboard
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.detach(AppointmentsDetails.this).attach(AppointmentsDetails.this).commit();
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

    /**
     * errorChecking live checks the formatting of fields; errors are highlighted to the user
     * and saving is disabled until they are corrected.
     * @param et1 is the appointment title, which cannot be empty
     * @param et2 is the appointment time, which must be a valid time
     * @param et3 is the appointment date, which must be a valid date
     */
    public void errorChecking(EditText et1, EditText et2, EditText et3){

        final EditText name = et1;
        final EditText time = et2;
        final EditText date = et3;

        // name format checking
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (name.getText().length() != 0){validTitle = true;} // valid name
                else {validTitle = false; name.setError("Name cannot be empty");} // empty name

                // disable/enable save button following format checks
                if (validTitle && validTime && validDate) {editMenu.getItem(0).setEnabled(true);}
                else {editMenu.getItem(0).setEnabled(false);}
            }

            // not needed for our purposes
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void afterTextChanged(Editable editable) {}
        });

        // time error checking
        time.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String t = time.getText().toString();
                if (t.length() == 5 && t.charAt(2) == ':') { // valid format
                    validTime = true;
                    String[] spl = t.split(":");
                    int hr = Integer.parseInt(spl[0]);
                    int min = Integer.parseInt(spl[1]);
                    if (spl[0].length() != 2 || spl[1].length() != 2 || hr < 0 || hr > 23 || min < 0 || min > 59) { // invalid value
                        validTime = false;
                        time.setError("Invalid time (HH:MM)");
                    }
                    else {validTime = true;} // valid value
                } else {validTime = false; time.setError("Invalid time (HH:MM)");} // invalid format


                // disable/enable save button following format checks
                if (validTitle && validTime && validDate) {editMenu.getItem(0).setEnabled(true);}
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
                if (validTitle && validTime && validDate) {editMenu.getItem(0).setEnabled(true);}
                else {editMenu.getItem(0).setEnabled(false);}
            }

            // not needed for our purposes
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void afterTextChanged(Editable editable) {}
        });
    }

    public void sendNotification() {

        final Context mContext = this.getContext();

        EditText time = rootView.findViewById(R.id.app_time);
        EditText date = rootView.findViewById(R.id.app_date);

        final EditText name = rootView.findViewById(R.id.appointments_title);
        final EditText location = rootView.findViewById(R.id.app_location);

        System.out.println(thisappointment.getReminders());

        if (thisappointment.getReminders()) {

            // Time variables
            int hourToSet = Integer.parseInt(time.getText().toString().substring(0,2));
            int minuteToSet = Integer.parseInt(time.getText().toString().substring(3,5));

            // Date variables
            int yearToSet = Integer.parseInt(date.getText().toString().substring(6,10));
            int monthToSet = Integer.parseInt(date.getText().toString().substring(3,5));
            int dayToSet = Integer.parseInt(date.getText().toString().substring(0,2));

            AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

            Intent intentAlarm = new Intent(mContext, AlarmReceiver.class);
            // Send the name of the medicine and whether notification should be descriptive to AlarmReceiver
            intentAlarm.putExtra("type", "appointment");
            intentAlarm.putExtra("location", location.getText().toString());
            intentAlarm.putExtra("appointment", name.getText().toString());
            intentAlarm.putExtra("descriptive", thisappointment.getReminder_type());
            PendingIntent notifySender = PendingIntent.getBroadcast(mContext, thisappointment.getUid(), intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);

            // Set notification to launch at medicine reminder time
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(yearToSet, monthToSet, dayToSet);
            calendar.set(Calendar.HOUR_OF_DAY, hourToSet);
            calendar.set(Calendar.MINUTE, minuteToSet);
            calendar.set(Calendar.SECOND, 0);

            if (thisappointment.isRemind_week()) {
                // Set for a week before the appointment date
                calendar.add(Calendar.DATE, -7);
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), notifySender);
                System.out.println(calendar.toString());
                System.out.println("week reminders");
            }

            if (thisappointment.isRemind_day()) {
                // Set for a day before the appointment date
                calendar.add(Calendar.DATE, -1);
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), notifySender);
                System.out.println("day reminders");
            }

            if (thisappointment.isRemind_morning()) {
                // Set for morning before the appointment date
                calendar.set(Calendar.HOUR_OF_DAY, 9);
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), notifySender);
                System.out.println("morning reminders");
            }
        }
    }

    public void cancelNotification() {
        final Context mContext = this.getContext();
        Intent intent = new Intent(mContext, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, thisappointment.getUid(), intent, 0);
        AlarmManager alarmManager = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
}