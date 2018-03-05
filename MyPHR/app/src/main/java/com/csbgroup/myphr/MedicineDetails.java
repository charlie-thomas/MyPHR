package com.csbgroup.myphr;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.csbgroup.myphr.database.AppDatabase;
import com.csbgroup.myphr.database.MedicineEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MedicineDetails extends Fragment {

    private MedicineEntity thismedicine; // the medicine we're viewing now

    private Menu editMenu;
    private String mode = "view";
    private View rootView;

    // key listeners and backgrounds for toggling field editability
    private KeyListener nameKL, descriptionKL, doseKL, notesKL, timeKL, dateKL;
    private Drawable nameBG, descriptionBG, doseBG, notesBG, timeBG, dateBG;

    // error checking booleans
    private Boolean validName = true;
    private Boolean validTime = true;
    private Boolean validDate = true;

    public MedicineDetails() {
        // Required empty public constructor
    }

    public static MedicineDetails newInstance() {
        MedicineDetails fragment = new MedicineDetails();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_medicine_details, container, false);
        this.rootView = rootView;

        Bundle args = getArguments();
        MedicineEntity medicine = getMedicine(Integer.valueOf(args.getString("uid")));
        thismedicine = medicine;

        final EditText name = rootView.findViewById(R.id.medicine_title);
        final EditText description = rootView.findViewById(R.id.medicine_info);
        EditText dose = rootView.findViewById(R.id.medicine_dose);
        EditText remtext = rootView.findViewById(R.id.reminder_time_title);
        EditText remtime = rootView.findViewById(R.id.reminder_time);
        EditText datetext = rootView.findViewById(R.id.reminder_date_title);
        EditText remdate = rootView.findViewById(R.id.reminder_date);
        EditText notes = rootView.findViewById(R.id.medicine_notes);

        // fill in the values
        name.setText(medicine.getTitle());
        description.setText(medicine.getDescription());
        dose.setText(medicine.getDose());
        notes.setText(medicine.getNotes());
        remtime.setText(medicine.getTime());
        remdate.setText(medicine.getDate());

        // save listeners and backgrounds
        nameBG = name.getBackground();
        nameKL = name.getKeyListener();
        descriptionKL = description.getKeyListener();
        descriptionBG = description.getBackground();
        doseKL = dose.getKeyListener();
        doseBG = dose.getBackground();
        timeBG = remtime.getBackground();
        timeKL = remtime.getKeyListener();
        dateBG = remdate.getBackground();
        dateKL = remdate.getKeyListener();
        notesKL = notes.getKeyListener();
        notesBG = notes.getBackground();

        // disable editability
        disableEditing(name);
        disableEditing(description);
        disableEditing(dose);
        disableEditing(remtext);
        disableEditing(remtime);
        disableEditing(datetext);
        disableEditing(remdate);
        disableEditing(notes);

        Switch reminders = rootView.findViewById(R.id.reminder_switch);
        reminders.setChecked(medicine.getReminders());

        RadioButton daily = rootView.findViewById(R.id.daily);
        RadioButton otherdays = rootView.findViewById(R.id.everyotherday);

        RadioButton general = rootView.findViewById(R.id.general);
        RadioButton descriptive = rootView.findViewById(R.id.descriptive);

        // check daily/otherdays radios to reflect database
        if (!thismedicine.isDaily()){
            daily.setChecked(false);
            otherdays.setChecked(true);
        }
        else{
            otherdays.setChecked(false);
            daily.setChecked(true);
        }

        // check general/descriptive radios to reflect database
        if (thismedicine.getReminder_type() == 0){
            descriptive.setChecked(false);
            general.setChecked(true);
        }
        else {
            general.setChecked(false);
            descriptive.setChecked(true);
        }

        // update the database when daily/otherdays radio buttons are changed
        RadioGroup radioGroup = rootView.findViewById(R.id.radios);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.daily:
                        thismedicine.setOther_days(false);
                        thismedicine.setDaily(true);
                        break;
                    case R.id.everyotherday:
                        thismedicine.setDaily(false);
                        thismedicine.setOther_days(true);
                        break;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        AppDatabase db = AppDatabase.getAppDatabase(getActivity());
                        db.medicineDao().update(thismedicine);
                    }
                }).start();
            }
        });

        // update the database when general/descriptive radio buttons are changed
        RadioGroup radioGroup2 = rootView.findViewById(R.id.radios2);
        radioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.general:
                        thismedicine.setReminder_type(0);
                        break;
                    case R.id.descriptive:
                        thismedicine.setReminder_type(1);
                        break;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        AppDatabase db = AppDatabase.getAppDatabase(getActivity());
                        db.medicineDao().update(thismedicine);
                    }
                }).start();
            }
        });

        // show the reminders options if reminders are on
        if (medicine.getReminders()) {
            daily.setVisibility(View.VISIBLE);
            otherdays.setVisibility(View.VISIBLE);
            general.setVisibility(View.VISIBLE);
            descriptive.setVisibility(View.VISIBLE);
            remtext.setVisibility(View.VISIBLE);
            remtime.setVisibility(View.VISIBLE);
            datetext.setVisibility(View.VISIBLE);
            remdate.setVisibility(View.VISIBLE);
        }

        // hide/show reminders options as switch is toggled
        reminders.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        AppDatabase db = AppDatabase.getAppDatabase(getActivity());
                        thismedicine.setReminders(isChecked);
                        db.medicineDao().update(thismedicine);
                    }
                }).start();

                RadioButton daily = rootView.findViewById(R.id.daily);
                RadioButton otherdays = rootView.findViewById(R.id.everyotherday);
                RadioButton general = rootView.findViewById(R.id.general);
                RadioButton descriptive = rootView.findViewById(R.id.descriptive);
                EditText remtime = rootView.findViewById(R.id.reminder_time);
                EditText remtext = rootView.findViewById(R.id.reminder_time_title);
                EditText remdate = rootView.findViewById(R.id.reminder_date);
                EditText datetext = rootView.findViewById(R.id.reminder_date_title);

                if (isChecked) { // reminders are on
                    sendNotification();

                    daily.setVisibility(View.VISIBLE);
                    otherdays.setVisibility(View.VISIBLE);
                    general.setVisibility(View.VISIBLE);
                    descriptive.setVisibility(View.VISIBLE);
                    remtext.setVisibility(View.VISIBLE);
                    datetext.setVisibility(View.VISIBLE);
                    remtime.setVisibility(View.VISIBLE);
                    remdate.setVisibility(View.VISIBLE);
                }
                else { // reminders are off
                    cancelNotification();

                    daily.setVisibility(View.GONE);
                    otherdays.setVisibility(View.GONE);
                    general.setVisibility(View.GONE);
                    descriptive.setVisibility(View.GONE);
                    remtext.setVisibility(View.GONE);
                    datetext.setVisibility(View.GONE);
                    remtime.setVisibility(View.GONE);
                    remdate.setVisibility(View.GONE);
               }
            }
        });

        // back button
        ((MainActivity) getActivity()).setToolbar("", true);
        setHasOptionsMenu(true);

        return rootView;
    }

    /**
     * Fetches a single medicine entity from the database, found by the title
     * @param uid is the primary key of the medicine to be retrieved
     * @return the medicine entity
     */
    private MedicineEntity getMedicine(final int uid) {

        // Create a callable object for database transactions
        Callable callable = new Callable() {
            @Override
            public Object call() throws Exception {
                return AppDatabase.getAppDatabase(getActivity()).medicineDao().getMedicine(uid);
            }
        };

        ExecutorService service = Executors.newFixedThreadPool(2);
        Future<MedicineEntity> result = service.submit(callable);

        MedicineEntity medicine = null;
        try {
            medicine = result.get();
        } catch (Exception e) {}

        return medicine;
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
     * @param item the clicked menu item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // back button - go back
                ((MainActivity) getActivity()).switchFragment(MedicineSection.newInstance());
                return true;

            case R.id.details_edit: // edit button - edit medicine details
                switchMode();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * switchMode toggles between viewing and editing the medicine details.
     */
    public void switchMode() {

        final EditText title = rootView.findViewById(R.id.medicine_title);
        final EditText description = rootView.findViewById(R.id.medicine_info);
        final EditText dose = rootView.findViewById(R.id.medicine_dose);
        final EditText notes = rootView.findViewById(R.id.medicine_notes);
        final EditText remtime = rootView.findViewById(R.id.reminder_time);
        final EditText remdate = rootView.findViewById(R.id.reminder_date);
        final Button delete = rootView.findViewById(R.id.delete);

        if (this.mode.equals("view")) { // entering edit mode
            editMenu.getItem(0).setIcon(R.drawable.tick);

            // activate error checking
            errorChecking(title, remtime, remdate);

            // show the delete button
            delete.setVisibility(View.VISIBLE);

            // restore bg and kl to make editable
            title.setBackground(nameBG);
            title.setKeyListener(nameKL);
            description.setKeyListener(descriptionKL);
            description.setBackground(descriptionBG);
            dose.setKeyListener(doseKL);
            dose.setBackground(doseBG);
            notes.setKeyListener(notesKL);
            notes.setBackground(notesBG);
            remtime.setKeyListener(timeKL);
            remtime.setBackground(timeBG);
            remdate.setKeyListener(dateKL);
            remdate.setBackground(dateBG);

            // confirm medicine deletion
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // set up the view
                    LayoutInflater inflater = getActivity().getLayoutInflater(); // get inflater
                    View v = inflater.inflate(R.layout.confirm_delete, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setView(v);
                    final TextView message = v.findViewById(R.id.message);
                    message.setText("Are you sure you want to delete " + thismedicine.getTitle() + "?");

                    // delete the contact
                    builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    AppDatabase db = AppDatabase.getAppDatabase(getActivity());
                                    db.medicineDao().delete(thismedicine);
                                    ((MainActivity) getActivity()).switchFragment(Medicine.newInstance());
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
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                }
            });

            this.mode = "edit";
            return;
        }

        if (this.mode.equals("edit")) { // exiting edit mode
            sendNotification();

            editMenu.getItem(0).setIcon(R.drawable.edit);

            // hide the delete button
            delete.setVisibility(View.GONE);

            // disable editing of all fields
            disableEditing(title);
            disableEditing(description);
            disableEditing(dose);
            disableEditing(notes);
            disableEditing(remtime);
            disableEditing(remdate);

            // update the medicine in the database
            new Thread(new Runnable() {
                @Override
                public void run() {
                    AppDatabase db = AppDatabase.getAppDatabase(getActivity());
                    thismedicine.setTitle(title.getText().toString());
                    thismedicine.setDescription(description.getText().toString());
                    thismedicine.setDose(dose.getText().toString());
                    thismedicine.setNotes(notes.getText().toString());
                    thismedicine.setTime(remtime.getText().toString());
                    thismedicine.setDate(remdate.getText().toString());
                    db.medicineDao().update(thismedicine);

                    // refresh to get rid of keyboard
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.detach(MedicineDetails.this).attach(MedicineDetails.this).commit();
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
     * @param et1 is the medicine name, which cannot be empty
     * @param et2 is the reminder time, which must be a valid time
     * @param et3 is the reminder date, which must be a valid date
     */
    public void errorChecking(EditText et1, EditText et2, EditText et3){

        final EditText name = et1;
        final EditText time = et2;
        final EditText date = et3;

        // name format checking
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (name.getText().length() != 0){validName = true;} // valid name
                else {validName = false; name.setError("Name cannot be empty");} // empty name

                // disable/enable save button following format checks
                if (validName && validTime && validDate) {editMenu.getItem(0).setEnabled(true);}
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
                if (validName && validTime && validDate) {editMenu.getItem(0).setEnabled(true);}
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
                if (d.length() != 10) {validDate = false; date.setError("Invalid date (DD/MM/YYYY)");} // invalid format
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
                if (validName && validTime && validDate) {editMenu.getItem(0).setEnabled(true);}
                else {editMenu.getItem(0).setEnabled(false);}
            }

            // not needed for our purposes
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void afterTextChanged(Editable editable) {}
        });
    }

    public void sendNotification() {

        final Context mContext = this.getContext();

        EditText remtime = rootView.findViewById(R.id.reminder_time);
        EditText remdate = rootView.findViewById(R.id.reminder_date);

        final EditText name = rootView.findViewById(R.id.medicine_title);

        System.out.println(thismedicine.getReminders());

        if (thismedicine.getReminders()) {

            // Time variables
            int hourToSet = Integer.parseInt(remtime.getText().toString().substring(0,2));
            int minuteToSet = Integer.parseInt(remtime.getText().toString().substring(3,5));

            // Date variables
            int yearToSet = Integer.parseInt(remdate.getText().toString().substring(6,10));
            int monthToSet = Integer.parseInt(remdate.getText().toString().substring(3,5));
            int dayToSet = Integer.parseInt(remdate.getText().toString().substring(0,2));

            AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

            Intent intentAlarm = new Intent(mContext, AlarmReceiver.class);
            // Send the name of the medicine and whether notification should be descriptive to AlarmReceiver
            intentAlarm.putExtra("medicine", name.getText().toString());
            intentAlarm.putExtra("descriptive", thismedicine.getReminder_type());
            PendingIntent notifySender = PendingIntent.getBroadcast(mContext, thismedicine.getUid(), intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);

            // Set notification to launch at medicine reminder time
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(yearToSet, monthToSet, dayToSet);
            calendar.set(Calendar.HOUR_OF_DAY, hourToSet);
            calendar.set(Calendar.MINUTE, minuteToSet);
            calendar.set(Calendar.SECOND, 0);

            if (thismedicine.isDaily()) {

                //if(calSet.compareTo(calNow) <= 0){
                    //Today Set time passed, count to tomorrow
                //    calSet.add(Calendar.DATE, 1);
                //}

                // If medicine is daily, repeat notification daily
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, notifySender);
            } else {
                // Else repeat every other day
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000 * 60 * 60 * 48, notifySender);
            }
        } else {
            System.out.println("Ignored notification because reminders are not set");
        }
    }

    public void cancelNotification() {
        final Context mContext = this.getContext();
        Intent intent = new Intent(mContext, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, thismedicine.getUid(), intent, 0);
        AlarmManager alarmManager = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
}