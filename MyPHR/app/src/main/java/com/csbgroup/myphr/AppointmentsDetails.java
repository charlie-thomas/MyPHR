package com.csbgroup.myphr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.csbgroup.myphr.database.AppDatabase;
import com.csbgroup.myphr.database.AppointmentsEntity;
import com.csbgroup.myphr.database.MedicineEntity;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AppointmentsDetails extends Fragment {

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

        Bundle args = getArguments();
        AppointmentsEntity appointment = getAppointment(args.getString("title"));

        TextView title = rootView.findViewById(R.id.appointments_title);
        title.setText(appointment.getTitle());

        TextView location = rootView.findViewById(R.id.app_location);
        location.setText(appointment.getLocation());

        TextView date = rootView.findViewById(R.id.app_date);
        date.setText(appointment.getDate());

        TextView time = rootView.findViewById(R.id.app_time);
        time.setText(appointment.getTime());

        TextView notes = rootView.findViewById(R.id.notes);
        notes.setText(appointment.getNotes());

        // back button
        ((MainActivity) getActivity()).setToolbar("My Appointments", true);
        setHasOptionsMenu(true);

        return rootView;
    }

    /**
     * Fetches a single appointment from the database, found by title
     * @param title is the title of the appointment to be retrieved
     * @return the appointment entity
     */
    private AppointmentsEntity getAppointment(final String title) {

        // Create a callable object for database transactions
        Callable callable = new Callable() {
            @Override
            public Object call() throws Exception {
                return AppDatabase.getAppDatabase(getActivity()).appointmentsDao().getAppointment(title);
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
    }

    /**
     * Provides navigation for menu items; currently only needed for navigation back to the
     * main appointments fragment.
     * @param item is the clicked menu item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // back button
                ((MainActivity) getActivity()).switchFragment(AppointmentsSection.newInstance());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}