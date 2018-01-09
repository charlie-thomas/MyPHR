package com.csbgroup.myphr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.csbgroup.myphr.database.AppDatabase;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Appointments extends Fragment {

    public Appointments() {
        // Required empty public constructor
    }

    public static Appointments newInstance() {
        Appointments fragment = new Appointments();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_appointments, container, false);

        ((MainActivity) getActivity()).setToolbar("My Appointments", false);
        setHasOptionsMenu(true);

        // Get all the appointments from the database
        List<String> appointments = getAppointmentNames();
        if (appointments == null) return rootView;

        ArrayAdapter<String> appointmentsAdapter = new ArrayAdapter<>(
                getActivity(),
                R.layout.simple_list_item,
                appointments);

        ListView listView = rootView.findViewById(R.id.appointments_list);
        listView.setAdapter(appointmentsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment details = AppointmentsDetails.newInstance();

                // Create a bundle to pass the medicine name to the details fragment
                Bundle bundle = new Bundle();
                bundle.putString("title", parent.getAdapter().getItem(position).toString());
                details.setArguments(bundle);

                ((MainActivity) getActivity()).switchFragment(details);
            }
        });

        return rootView;
    }

    private List<String> getAppointmentNames() {
        // Create a callable object for database transactions
        Callable callable = new Callable() {
            @Override
            public Object call() throws Exception {
                return AppDatabase.getAppDatabase(getActivity()).appointmentsDao().getAllTitles();
            }
        };

        // Get a Future object of all the appointment names
        ExecutorService service = Executors.newFixedThreadPool(2);
        Future<List<String>> result = service.submit(callable);

        // Create a list of the appointment names
        List<String> appointments = null;
        try {
            appointments = result.get();
        } catch (Exception e) {}

        return appointments;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.settings, menu);
    }

    /* Navigation from Appointments to settings fragment */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            ((MainActivity) getActivity()).switchFragment(AppointmentsSettings.newInstance());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
