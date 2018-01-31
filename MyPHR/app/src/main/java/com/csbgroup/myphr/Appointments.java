package com.csbgroup.myphr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import com.csbgroup.myphr.database.AppDatabase;
import com.csbgroup.myphr.database.AppointmentsEntity;
import com.csbgroup.myphr.database.MedicineEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Appointments extends Fragment {

    private FloatingActionButton fab; // the add appointment fab

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

        // view set up
        View rootView = inflater.inflate(R.layout.fragment_appointments, container, false);
        ((MainActivity) getActivity()).setToolbar("My Appointments", false);
        setHasOptionsMenu(true);

        // fetch appointments entities from database
        List<AppointmentsEntity> apps = getAppointments();
        if (apps == null) return rootView;
        List<String> appointments = new ArrayList<>();
        for (AppointmentsEntity ap : apps) {
            appointments.add(ap.getTitle());
        }

        // display the appointments in list
        ArrayAdapter<String> appointmentsAdapter = new ArrayAdapter<>(
                getActivity(),
                R.layout.simple_list_item,
                appointments);
        ListView listView = rootView.findViewById(R.id.appointments_list);
        listView.setAdapter(appointmentsAdapter);

        // switching to details fragment
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment details = AppointmentsDetails.newInstance();

                // Create a bundle to pass the appointment to the details fragment
                Bundle bundle = new Bundle();
                bundle.putString("title", parent.getAdapter().getItem(position).toString());
                details.setArguments(bundle);

                ((MainActivity) getActivity()).switchFragment(details);
            }
        });

        // fab action for adding appointment
        fab = rootView.findViewById(R.id.app_fab);
        buildDialog(fab);

        return rootView;
    }

    /**
     * getAppointments fetches the list of appointments from the database
     * @return the list of appointment entities
     */
    private List<AppointmentsEntity> getAppointments() {

        // Create a callable object for database transactions
        Callable callable = new Callable() {
            @Override
            public Object call() throws Exception {
                return AppDatabase.getAppDatabase(getActivity()).appointmentsDao().getAll();
            }
        };

        // Get a Future object of all the appointment titles
        ExecutorService service = Executors.newFixedThreadPool(2);
        Future<List<AppointmentsEntity>> result = service.submit(callable);

        // Create a list of the appointment names
        List<AppointmentsEntity> appointments = null;
        try {
            appointments = result.get();
        } catch (Exception e) {}

        return appointments;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    /**
     * Provides navigation for menu items; currently only needed for navigation to settings
     * fragment.
     * @param item is the clicked menu item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            ((MainActivity) getActivity()).switchFragment(AppointmentsSettings.newInstance());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * buildDialog builds the pop-up dialog for adding a new appointment
     * @param fab the floating action button which pulls up the dialog
     */
    public void buildDialog(FloatingActionButton fab) {

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // set up the dialog
                LayoutInflater inflater = getActivity().getLayoutInflater(); // get inflater
                View v = inflater.inflate(R.layout.add_appointment_dialog, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setView(v);

                // fetch the input values
                final EditText title = v.findViewById(R.id.appointment_name);
                final EditText location = v.findViewById(R.id.appointment_location);
                final EditText day = v.findViewById(R.id.appointment_DD);
                final EditText month = v.findViewById(R.id.appointment_MM);
                final EditText year = v.findViewById(R.id.appointment_YYYY);
                final EditText time = v.findViewById(R.id.appointment_time);
                final EditText notes = v.findViewById(R.id.appointment_notes);

                // auto shift view focus when entering date
                shiftFocus(day, month, year, time);

                // add new appointment action
                builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        new Thread(new Runnable(){
                            @Override
                            public void run(){

                                // join date into one string
                                String date = day.getText().toString() + "/" + month.getText().toString()
                                        + "/" + year.getText().toString();

                                // add the new appointment to the database
                                AppDatabase db = AppDatabase.getAppDatabase(getActivity());
                                AppointmentsEntity appointment = new AppointmentsEntity(
                                        title.getText().toString(), location.getText().toString(),
                                        date, time.getText().toString(),
                                        notes.getText().toString(), false);
                                db.appointmentsDao().insertAll(appointment);
                            }
                        }).start();

                        // Move to details for new appointment
                        Fragment newdetails = AppointmentsDetails.newInstance();
                        Bundle bundle = new Bundle();
                        bundle.putString("title", title.getText().toString());
                        newdetails.setArguments(bundle);
                        ((MainActivity)getActivity()).switchFragment(newdetails);
                    }
                    // TODO: should redirect to details fragment upon ADD
                });

                // action for cancelling activity
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    /**
     * shiftFocus automatically shifts the fab dialog view focus from day->month and month->year
     * when two digits have been entered for day and month, respectively.
     * @param day is the EditText for the dialog day('DD') field
     * @param month is the EditText for the dialog month('MM') field
     * @param year is the EditText for the dialog year('YYYY') field
     * @param next is the EditText for the dialog field that follows year
     */
    public void shiftFocus(final EditText day, final EditText month, final EditText year, final EditText next){

        day.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (day.getText().toString().length() == 2) {month.requestFocus();}
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        month.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (month.getText().toString().length() == 2) {year.requestFocus();}
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        year.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (year.getText().toString().length() == 4) {next.requestFocus();}
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

    }

}
