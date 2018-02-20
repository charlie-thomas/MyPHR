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

    private KeyListener namelistener, descriptionlistener, doselistener, noteslistener, timelistener, datelistener;
    private Drawable namebackground, descriptionbackground, dosebackground, notesbackground, timebackground, datebackground;

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

        // fill in the values
        Bundle args = getArguments();
        MedicineEntity medicine = getMedicine(Integer.valueOf(args.getString("uid")));
        this.thismedicine = medicine;

        EditText name = rootView.findViewById(R.id.medicine_title);
        name.setText(medicine.getTitle());
        namebackground = name.getBackground();
        namelistener = name.getKeyListener();
        name.setKeyListener(null);
        name.setBackground(null);

        EditText description = rootView.findViewById(R.id.medicine_info);
        description.setText(medicine.getDescription());
        descriptionlistener = description.getKeyListener();
        descriptionbackground = description.getBackground();
        description.setBackground(null);
        description.setKeyListener(null);

        EditText dose = rootView.findViewById(R.id.medicine_dose);
        dose.setText(medicine.getDose());
        doselistener = dose.getKeyListener();
        dosebackground = dose.getBackground();
        dose.setBackground(null);
        dose.setKeyListener(null);

        EditText remtext = rootView.findViewById(R.id.reminder_time_title);
        remtext.setKeyListener(null);
        remtext.setBackground(null);

        EditText remtime = rootView.findViewById(R.id.reminder_time);
        timebackground = remtime.getBackground();
        timelistener = remtime.getKeyListener();
        remtime.setKeyListener(null);
        remtime.setBackground(null);

        EditText datetext = rootView.findViewById(R.id.reminder_date_title);
        datetext.setKeyListener(null);
        datetext.setBackground(null);

        EditText remdate = rootView.findViewById(R.id.reminder_date);
        datebackground = remdate.getBackground();
        datelistener = remdate.getKeyListener();
        remdate.setKeyListener(null);
        remdate.setBackground(null);

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

        if (medicine.getReminders()) {
            daily.setVisibility(View.VISIBLE);
            otherdays.setVisibility(View.VISIBLE);
            remtext.setVisibility(View.VISIBLE);
            remtime.setVisibility(View.VISIBLE);
            datetext.setVisibility(View.VISIBLE);
            remdate.setVisibility(View.VISIBLE);

            remtime.setText(thismedicine.getTime());
            remdate.setText(thismedicine.getDate());
        }

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

                if (isChecked) {
                    daily.setVisibility(View.VISIBLE);
                    otherdays.setVisibility(View.VISIBLE);
                    remtext.setVisibility(View.VISIBLE);
                    datetext.setVisibility(View.VISIBLE);
                    remtime.setVisibility(View.VISIBLE);
                    remtime.setText(thismedicine.getTime());
                    remdate.setVisibility(View.VISIBLE);
                    remdate.setText(thismedicine.getDate());
                }
                else {
                    daily.setVisibility(View.GONE);
                    otherdays.setVisibility(View.GONE);
                    remtext.setVisibility(View.GONE);
                    datetext.setVisibility(View.GONE);
                    remtime.setVisibility(View.GONE);
                    remdate.setVisibility(View.GONE);
               }
            }
        });

        EditText notes = rootView.findViewById(R.id.medicine_notes);
        notes.setText(medicine.getNotes());
        noteslistener = notes.getKeyListener();
        notesbackground = notes.getBackground();
        notes.setKeyListener(null);
        notes.setBackground(null);

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

        if (this.mode.equals("view")) {
            editMenu.getItem(0).setIcon(R.drawable.tick);

            EditText title = rootView.findViewById(R.id.medicine_title);
            title.setText(thismedicine.getTitle());
            title.setBackground(namebackground);
            title.setKeyListener(namelistener);

            EditText description = rootView.findViewById(R.id.medicine_info);
            description.setText(thismedicine.getDescription());
            description.setKeyListener(descriptionlistener);
            description.setBackground(descriptionbackground);

            EditText dose = rootView.findViewById(R.id.medicine_dose);
            dose.setText(thismedicine.getDose());
            dose.setKeyListener(doselistener);
            dose.setBackground(dosebackground);

            EditText notes = rootView.findViewById(R.id.medicine_notes);
            notes.setText(thismedicine.getNotes());
            notes.setKeyListener(noteslistener);
            notes.setBackground(notesbackground);

            EditText remtime = rootView.findViewById(R.id.reminder_time);
            remtime.setText(thismedicine.getTime());
            remtime.setKeyListener(timelistener);
            remtime.setBackground(timebackground);

            EditText remdate = rootView.findViewById(R.id.reminder_date);
            remdate.setText(thismedicine.getDate());
            remdate.setKeyListener(datelistener);
            remdate.setBackground(datebackground);

            //TODO: make delete button appear

            this.mode = "edit";
            return;
        }

        if (this.mode.equals("edit")) {
            editMenu.getItem(0).setIcon(R.drawable.edit);

            final EditText title = rootView.findViewById(R.id.medicine_title);
            title.setKeyListener(null);
            title.setBackground(null);

            final EditText description = rootView.findViewById(R.id.medicine_info);
            description.setKeyListener(null);
            description.setBackground(null);

            final EditText dose = rootView.findViewById(R.id.medicine_dose);
            dose.setKeyListener(null);
            dose.setBackground(null);

            final EditText notes = rootView.findViewById(R.id.medicine_notes);
            notes.setKeyListener(null);
            notes.setBackground(null);

            final EditText remtime = rootView.findViewById(R.id.reminder_time);
            remtime.setKeyListener(null);
            remtime.setBackground(null);

            final EditText remdate = rootView.findViewById(R.id.reminder_date);
            remdate.setKeyListener(null);
            remdate.setBackground(null);

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

}