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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.csbgroup.myphr.database.AppDatabase;
import com.csbgroup.myphr.database.MedicineEntity;

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

        EditText name = rootView.findViewById(R.id.medicine_title);
        EditText description = rootView.findViewById(R.id.medicine_info);
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

        if (!thismedicine.isDaily()){
            daily.setChecked(false);
            otherdays.setChecked(true);
        }
        else{
            otherdays.setChecked(false);
            daily.setChecked(true);
        }

        // update the database when radio buttons are changed
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

        // show the reminders options if reminders are on
        if (medicine.getReminders()) {
            daily.setVisibility(View.VISIBLE);
            otherdays.setVisibility(View.VISIBLE);
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
                EditText remtime = rootView.findViewById(R.id.reminder_time);
                EditText remtext = rootView.findViewById(R.id.reminder_time_title);
                EditText remdate = rootView.findViewById(R.id.reminder_date);
                EditText datetext = rootView.findViewById(R.id.reminder_date_title);

                if (isChecked) { // reminders are on
                    daily.setVisibility(View.VISIBLE);
                    otherdays.setVisibility(View.VISIBLE);
                    remtext.setVisibility(View.VISIBLE);
                    datetext.setVisibility(View.VISIBLE);
                    remtime.setVisibility(View.VISIBLE);
                    remdate.setVisibility(View.VISIBLE);
                }
                else { // reminders are off
                    daily.setVisibility(View.GONE);
                    otherdays.setVisibility(View.GONE);
                    remtext.setVisibility(View.GONE);
                    datetext.setVisibility(View.GONE);
                    remtime.setVisibility(View.GONE);
                    remdate.setVisibility(View.GONE);
               }
            }
        });

        // back button
        ((MainActivity) getActivity()).setToolbar("My Medicine", true);
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
                ((MainActivity) getActivity()).switchFragment(Medicine.newInstance());
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

            // delete the medicine
            delete.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            AppDatabase db = AppDatabase.getAppDatabase(getActivity());
                            db.medicineDao().delete(thismedicine);
                            ((MainActivity) getActivity()).switchFragment(MedicineSection.newInstance());
                        }
                    }).start();
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


}