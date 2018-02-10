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
import android.widget.EditText;
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

    private KeyListener descriptionlistener, doselistener, noteslistener;
    private Drawable descriptionbackground, dosebackground, notesbackground;

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

        View rootView = inflater.inflate(R.layout.fragment_medicine_details, container, false);
        this.rootView = rootView;

        // fill in the values

        Bundle args = getArguments();
        MedicineEntity medicine = getMedicine(args.getString("title"));
        this.thismedicine = medicine;

        // TODO: make this editable once Primary Key issue is resolved
        TextView name = rootView.findViewById(R.id.medicine_title);
        name.setText(medicine.getTitle());

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

        Switch reminders = rootView.findViewById(R.id.reminder_switch);
        reminders.setChecked(medicine.getReminders());

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
     * @param medTitle is the title of the medicine to be retrieved
     * @return the medicine entity
     */
    private MedicineEntity getMedicine(final String medTitle) {

        // Create a callable object for database transactions
        Callable callable = new Callable() {
            @Override
            public Object call() throws Exception {
                return AppDatabase.getAppDatabase(getActivity()).medicineDao().getMedicine(medTitle);
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

            //TODO: make delete button appear

            this.mode = "edit";
            return;
        }

        if (this.mode.equals("edit")) {
            editMenu.getItem(0).setIcon(R.drawable.edit);

            final EditText description = rootView.findViewById(R.id.medicine_info);
            description.setKeyListener(null);
            description.setBackground(null);

            final EditText dose = rootView.findViewById(R.id.medicine_dose);
            dose.setKeyListener(null);
            dose.setBackground(null);

            final EditText notes = rootView.findViewById(R.id.medicine_notes);
            notes.setKeyListener(null);
            notes.setBackground(null);

            this.mode = "view";
            return;
        }
    }
}