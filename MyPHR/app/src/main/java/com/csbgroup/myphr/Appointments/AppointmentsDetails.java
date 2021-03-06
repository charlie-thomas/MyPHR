package com.csbgroup.myphr.Appointments;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
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

import com.csbgroup.myphr.MainActivity;
import com.csbgroup.myphr.R;
import com.csbgroup.myphr.database.AppDatabase;
import com.csbgroup.myphr.database.AppointmentsEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AppointmentsDetails extends Fragment {

    private AppointmentsEntity thisappointment; // the appointment we're viewing now

    private String mode = "view"; // load in view mode
    private Menu editMenu;
    private View rootView;

    // key listeners and backgrounds for toggling field editability
    private KeyListener titleKL, locationKL, dateKL, timeKL, notesKL;
    private Drawable titleBG, locationBG, dateBG, timeBG, notesBG;

    // format error checking booleans
    private Boolean validTitle = true;
    private Boolean validTime = true;
    private Boolean validDate = true;

    public AppointmentsDetails() {} // Required empty public constructor

    public static AppointmentsDetails newInstance() {
        return new AppointmentsDetails();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_appointments_details, container, false);
        this.rootView = rootView;

        // receive the details from the main fragment
        Bundle args = getArguments();
        AppointmentsEntity appointment = getAppointment(Integer.parseInt(args.getString("uid")));
        thisappointment = appointment;

        EditText title = rootView.findViewById(R.id.appointments_title);
        EditText location = rootView.findViewById(R.id.app_location);
        EditText date = rootView.findViewById(R.id.app_date);
        EditText time = rootView.findViewById(R.id.app_time);
        EditText notes = rootView.findViewById(R.id.app_notes);
        RadioButton general = rootView.findViewById(R.id.general);
        RadioButton descriptive = rootView.findViewById(R.id.descriptive);
        CheckBox week = rootView.findViewById(R.id.checkBox1);
        CheckBox day = rootView.findViewById(R.id.checkBox2);
        CheckBox morning = rootView.findViewById(R.id.checkBox3);

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

        // set reminder switch to reflect database
        Switch reminders = rootView.findViewById(R.id.reminder_switch);
        reminders.setChecked(appointment.getReminders());

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
                        Appointments.sendNotification(thisappointment);
                        break;
                    case R.id.descriptive:
                        thisappointment.setReminder_type(1);
                        Appointments.sendNotification(thisappointment);
                        break;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        AppDatabase db = AppDatabase.getAppDatabase(getActivity());
                        db.appointmentsDao().update(thisappointment);
                    }
                }).start();

                // Checks which notification type the user wants *after* database updates
                Appointments.sendNotification(thisappointment);
            }
        });

        // update the database when week/day/morning checkboxes are changed
        week.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (isChecked) {
                    thisappointment.setRemind_week(true);
                }
                else {
                    thisappointment.setRemind_week(false);
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        AppDatabase db = AppDatabase.getAppDatabase(getActivity());
                        db.appointmentsDao().update(thisappointment);
                    }
                }).start();

                // Checks if user wants weekly reminders *after* database updates
                if (thisappointment.isRemind_week()) {
                    Appointments.sendNotification(thisappointment);
                } else {
                    Appointments.cancelNotification(thisappointment, 1000);
                }
            }
        });
        day.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (isChecked) {
                    thisappointment.setRemind_day(true);
                }
                else {
                    thisappointment.setRemind_day(false);
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        AppDatabase db = AppDatabase.getAppDatabase(getActivity());
                        db.appointmentsDao().update(thisappointment);
                    }
                }).start();

                // Checks if user wants daily reminders *after* database updates
                if (thisappointment.isRemind_day()) {
                    Appointments.sendNotification(thisappointment);
                } else {
                    Appointments.cancelNotification(thisappointment, 2000);
                }
            }
        });
        morning.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (isChecked) {
                    thisappointment.setRemind_morning(true);
                }
                else {
                    thisappointment.setRemind_morning(false);
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        AppDatabase db = AppDatabase.getAppDatabase(getActivity());
                        db.appointmentsDao().update(thisappointment);
                    }
                }).start();

                // Checks if user wants morning reminders *after* database updates
                if (thisappointment.isRemind_morning()) {
                    Appointments.sendNotification(thisappointment);
                } else {
                    Appointments.cancelNotification(thisappointment, 3000);
                }
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
                    Appointments.sendNotification(thisappointment);

                    general.setVisibility(View.VISIBLE);
                    descriptive.setVisibility(View.VISIBLE);
                    week.setVisibility(View.VISIBLE);
                    day.setVisibility(View.VISIBLE);
                    morning.setVisibility(View.VISIBLE);
                }
                else { // reminders are off
                    Appointments.cancelNotification(thisappointment, 1000);
                    Appointments.cancelNotification(thisappointment, 2000);
                    Appointments.cancelNotification(thisappointment,3000);

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
     * getAppointment etches a single appointment from the database.
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
        } catch (Exception e) {
            e.printStackTrace();
        }

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
     * onOptionsItemSelected provides navigation/actions for menu items.
     * @param item is the clicked menu item
     * @return super.onOptionsItemSelected(item)
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

        if (this.mode.equals("view")) { // entering edit mode
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

            editMenu.getItem(0).setIcon(R.drawable.edit);

            // hide the delete button
            delete.setVisibility(View.GONE);

            // disable editing of all fields
            disableEditing(title);
            disableEditing(location);
            disableEditing(date);
            disableEditing(time);
            disableEditing(notes);

            // update the appointment in the database
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

            new Thread(new Runnable() {
                @Override
                public void run() {
                    AppDatabase db = AppDatabase.getAppDatabase(getActivity());
                    db.appointmentsDao().update(thisappointment);
                }
            }).start();

            Appointments.sendNotification(thisappointment);
        }
    }


    /**
     * disableEditing sets an editText's background and keylistener to null to stop user editing.
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

        final EditText title = et1;
        final EditText time = et2;
        final EditText date = et3;

        // title format checking
        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (title.getText().length() != 0){validTitle = true;} // valid title
                else {validTitle = false; title.setError("Name cannot be empty");} // empty title

                // disable/enable save button following format checks
                if (validTitle && validTime && validDate) {editMenu.getItem(0).setEnabled(true);}
                else {editMenu.getItem(0).setEnabled(false);}
            }
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
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void afterTextChanged(Editable editable) {}
        });

        // date error checking
        date.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String d = date.getText().toString();
                if (d.length() != 10) {validDate = false; date.setError("Invalid date (DD/MM/YYYY)");} // invalid format
                else {
                    try { // valid format
                        validDate = true;
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
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
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void afterTextChanged(Editable editable) {}
        });
    }
}