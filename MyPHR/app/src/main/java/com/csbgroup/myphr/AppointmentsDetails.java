package com.csbgroup.myphr;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import com.csbgroup.myphr.database.AppDatabase;
import com.csbgroup.myphr.database.AppointmentsEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    // error checking booleans
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

        View rootView = inflater.inflate(R.layout.fragment_appointments_details, container, false);
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

            // deleting an appointment
            delete.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            AppDatabase db = AppDatabase.getAppDatabase(getActivity());
                            db.appointmentsDao().delete(thisappointment);
                            ((MainActivity) getActivity()).switchFragment(AppointmentsSection.newInstance());
                        }
                    }).start();
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


}