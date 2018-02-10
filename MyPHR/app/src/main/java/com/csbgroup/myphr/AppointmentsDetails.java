package com.csbgroup.myphr;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.csbgroup.myphr.database.AppDatabase;
import com.csbgroup.myphr.database.AppointmentsEntity;
import com.csbgroup.myphr.database.MedicineEntity;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AppointmentsDetails extends Fragment {

    private AppointmentsEntity thisappointment; // the appointment we're viewing now

    private Menu editMenu;
    private String mode = "view";
    private View rootView;

    private KeyListener locationlistener, datelistener, timelistener, noteslistener;
    private Drawable locationbackground, datebackground, timebackground, notesbackground;

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
        Log.d("ID", args.getString("uid"));
        AppointmentsEntity appointment = getAppointment(Integer.parseInt(args.getString("uid")));
        this.thisappointment = appointment;

        // TODO: make this editable once Primary Key issue is resolved
        TextView title = rootView.findViewById(R.id.appointments_title);
        title.setText(appointment.getTitle());

        EditText location = rootView.findViewById(R.id.app_location);
        location.setText(appointment.getLocation());
        locationlistener = location.getKeyListener();
        locationbackground = location.getBackground();
        location.setKeyListener(null);
        location.setBackground(null);

        EditText date = rootView.findViewById(R.id.app_date);
        date.setText(appointment.getDate());
        datelistener = date.getKeyListener();
        datebackground = date.getBackground();
        date.setBackground(null);
        date.setKeyListener(null);

        EditText time = rootView.findViewById(R.id.app_time);
        time.setText(appointment.getTime());
        timelistener = time.getKeyListener();
        timebackground = time.getBackground();
        time.setKeyListener(null);
        time.setBackground(null);

        EditText notes = rootView.findViewById(R.id.app_notes);
        notes.setText(appointment.getNotes());
        noteslistener = notes.getKeyListener();
        notesbackground = notes.getBackground();
        notes.setBackground(null);
        notes.setKeyListener(null);

        // back button
        ((MainActivity) getActivity()).setToolbar("My Appointments", true);
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

        if (this.mode.equals("view")) {
            editMenu.getItem(0).setIcon(R.drawable.tick);

            EditText location = rootView.findViewById(R.id.app_location);
            location.setText(thisappointment.getLocation());
            location.setKeyListener(locationlistener);
            location.setBackground(locationbackground);

            EditText date = rootView.findViewById(R.id.app_date);
            date.setText(thisappointment.getDate());
            date.setKeyListener(datelistener);
            date.setBackground(datebackground);

            EditText time = rootView.findViewById(R.id.app_time);
            time.setText(thisappointment.getTime());
            time.setKeyListener(timelistener);
            time.setBackground(timebackground);

            EditText notes = rootView.findViewById(R.id.app_notes);
            notes.setText(thisappointment.getNotes());
            notes.setKeyListener(noteslistener);
            notes.setBackground(notesbackground);

            //TODO: make delete button appear

            this.mode = "edit";
            return;
        }

        if (this.mode.equals("edit")){
            editMenu.getItem(0).setIcon(R.drawable.edit);

            final EditText location = rootView.findViewById(R.id.app_location);
            location.setKeyListener(null);
            location.setBackground(null);

            final EditText date = rootView.findViewById(R.id.app_date);
            date.setKeyListener(null);
            date.setBackground(null);

            final EditText time = rootView.findViewById(R.id.app_time);
            time.setKeyListener(null);
            time.setBackground(null);

            final EditText notes = rootView.findViewById(R.id.app_notes);
            notes.setKeyListener(null);
            notes.setBackground(null);

            this.mode = "view";
            return;
        }
    }

}