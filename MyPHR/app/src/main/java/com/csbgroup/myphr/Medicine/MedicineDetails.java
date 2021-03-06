package com.csbgroup.myphr.Medicine;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.csbgroup.myphr.MainActivity;
import com.csbgroup.myphr.R;
import com.csbgroup.myphr.database.AppDatabase;
import com.csbgroup.myphr.database.MedicineEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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


    public MedicineDetails() {}// Required empty public constructor

    public static MedicineDetails newInstance() {
        return new MedicineDetails();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // set up the view
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
        RadioButton daily = rootView.findViewById(R.id.daily);
        RadioButton otherdays = rootView.findViewById(R.id.everyotherday);
        RadioButton general = rootView.findViewById(R.id.general);
        RadioButton descriptive = rootView.findViewById(R.id.descriptive);

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

        // set reminders switch to reflect database
        Switch reminders = rootView.findViewById(R.id.reminder_switch);
        reminders.setChecked(medicine.getReminders());

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

                Medicine.sendNotification(thismedicine);
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

                // Checks which notification type the user wants *after* database updates
                Medicine.sendNotification(thismedicine);
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
                    Medicine.sendNotification(thismedicine);

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
                    Medicine.cancelNotification(thismedicine);

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
        editMenu = menu; // toolbar w/ edit button
    }


    /**
     * Provides navigation/actions for menu items.
     * @param item the clicked menu item
     * @return onOptionsItemSelected(item)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // back button - go back
                BottomNavigationView bn = getActivity().findViewById(R.id.bottom_nav);
                bn.setSelectedItemId(R.id.medicine);
                return true;

            case R.id.details_edit: // edit button - edit medication details
                switchMode();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /**
     * switchMode toggles between viewing and editing the medication details.
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
                                    ((MainActivity) getActivity()).switchFragment(Medicine.newInstance(), false);
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

        if (this.mode.equals("edit")) { // exiting edit mode

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

            // update the medication in the database
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

            new Thread(new Runnable() {
                @Override
                public void run() {
                    AppDatabase db = AppDatabase.getAppDatabase(getActivity());
                    db.medicineDao().update(thismedicine);
                }
            }).start();

            Medicine.sendNotification(thismedicine);
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
                    try {
                        int hr = Integer.parseInt(spl[0]);
                        int min = Integer.parseInt(spl[1]);
                        if (spl[0].length() != 2 || spl[1].length() != 2 || hr < 0 || hr > 23 || min < 0 || min > 59) { // invalid value
                            validTime = false;
                            time.setError("Invalid time (HH:MM)");
                        }
                        else {validTime = true;} // valid value
                    }
                    catch(Exception e){validTime = false; time.setError("Invalid time (HH:MM)");}
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
                if (validName && validTime && validDate) {editMenu.getItem(0).setEnabled(true);}
                else {editMenu.getItem(0).setEnabled(false);}
            }

            // not needed for our purposes
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void afterTextChanged(Editable editable) {}
        });
    }
}