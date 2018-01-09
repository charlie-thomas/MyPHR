package com.csbgroup.myphr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.csbgroup.myphr.database.AppDatabase;
import com.csbgroup.myphr.database.MedicineEntity;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MedicineDetails extends Fragment {

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

        Bundle args = getArguments();
        MedicineEntity medicine = getMedicine(args.getString("title"));

        TextView medTitle = rootView.findViewById(R.id.medicine_title);
        medTitle.setText(medicine.getTitle());

        TextView medInfo = rootView.findViewById(R.id.medicine_info);
        medInfo.setText(medicine.getDescription());

        Switch reminders = rootView.findViewById(R.id.reminder_switch);
        reminders.setChecked(medicine.getReminders());

        TextView notes = rootView.findViewById(R.id.notes);
        notes.setText(medicine.getNotes());

        ((MainActivity) getActivity()).setToolbar("My Medicine", true);
        setHasOptionsMenu(true);

        return rootView;
    }

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
    }

    /* Navigation from details fragment back to Medicine */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ((MainActivity) getActivity()).switchFragment(Medicine.newInstance());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}