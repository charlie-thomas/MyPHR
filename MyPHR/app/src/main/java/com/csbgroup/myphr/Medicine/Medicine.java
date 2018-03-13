package com.csbgroup.myphr.Medicine;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.csbgroup.myphr.AlarmReceiver;
import com.csbgroup.myphr.Calendar.CalendarEvent;
import com.csbgroup.myphr.MainActivity;
import com.csbgroup.myphr.R;
import com.csbgroup.myphr.Adapters.SimpleAdapter;
import com.csbgroup.myphr.database.AppDatabase;
import com.csbgroup.myphr.database.MedicineEntity;

import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Medicine extends Fragment {

    private static Object mContext;

    private FloatingActionButton fab; // add medicine fab

    public Medicine() {
    } // Required empty public constructor

    public static Medicine newInstance() {
        return new Medicine();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mContext = this.getContext();

        // view set up
        View rootView = inflater.inflate(R.layout.fragment_medicine, container, false);
        ((MainActivity) getActivity()).setToolbar("My Medication", false);
        setHasOptionsMenu(true);

        // fetch medicines entities from database
        List<MedicineEntity> medicines = getMedicines();
        if (medicines == null) return rootView;

        // Convert MedicineEntities into a map of their uid and titles
        List<Map.Entry<Integer, String>> medicine_map = new ArrayList<>();
        for (MedicineEntity me : medicines)
            medicine_map.add(new AbstractMap.SimpleEntry<>(me.getUid(), me.getTitle()));

        // display the medicines in list
        SimpleAdapter medicineAdapter = new SimpleAdapter(getActivity(), medicine_map);
        ListView listView = rootView.findViewById(R.id.medicine_list);
        listView.setAdapter(medicineAdapter);

        // switching to details fragment
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment details = MedicineDetails.newInstance();

                // Create a bundle to pass the medicine to the details fragment
                Bundle bundle = new Bundle();
                bundle.putString("uid", view.getTag().toString());
                details.setArguments(bundle);

                ((MainActivity) getActivity()).switchFragment(details, true);
            }
        });

        // display no medications message when meds empty
        LinearLayout nomeds = rootView.findViewById(R.id.no_meds);
        nomeds.setVisibility(View.INVISIBLE);
        if (listView.getAdapter().getCount() == 0) nomeds.setVisibility(View.VISIBLE);

        // fab action for adding medicine
        FloatingActionButton fab = rootView.findViewById(R.id.med_fab);
        buildDialog(fab);

        return rootView;
    }

    /**
     * getMedicines fetches the list of medicines from the database
     *
     * @return the list of medicine entities
     */
    private List<MedicineEntity> getMedicines() {

        // Create a callable object for database transactions
        Callable callable = new Callable() {
            @Override
            public Object call() throws Exception {
                return AppDatabase.getAppDatabase(getActivity()).medicineDao().getAll();
            }
        };

        // Get a Future object of all the medicine titles
        ExecutorService service = Executors.newFixedThreadPool(2);
        Future<List<MedicineEntity>> result = service.submit(callable);

        // Create a list of the medicine names
        List<MedicineEntity> medicines = null;
        try {
            medicines = result.get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return medicines;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * buildDialog builds the pop-up dialog for adding a new medicine, with input format checking.
     *
     * @param fab the floating action button which pulls up the dialog
     */
    public void buildDialog(FloatingActionButton fab) {

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // set up the dialog
                LayoutInflater inflater = getActivity().getLayoutInflater(); // get inflater
                @SuppressLint("InflateParams") View v = inflater.inflate(R.layout.add_medicine_dialog, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setView(v);

                // fetch the input values
                final EditText name = v.findViewById(R.id.med_name);
                final EditText description = v.findViewById(R.id.med_description);
                final EditText dose = v.findViewById(R.id.med_dose);
                final EditText notes = v.findViewById(R.id.med_notes);

                // add new medicine action
                builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        // Add the new medicine to the database
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                AppDatabase db = AppDatabase.getAppDatabase(getActivity());
                                @SuppressLint("SimpleDateFormat") MedicineEntity medicine = new MedicineEntity(name.getText().toString(),
                                        description.getText().toString(), dose.getText().toString(),
                                        notes.getText().toString(), false, 0, true, false,
                                        new SimpleDateFormat("dd/MM/yyyy").format(new Date()), //today's date
                                        "00:00");
                                long uid = db.medicineDao().insert(medicine);

                                // Move to details for new medicine
                                Fragment newdetails = MedicineDetails.newInstance();
                                Bundle bundle = new Bundle();
                                bundle.putString("uid", String.valueOf(uid));
                                newdetails.setArguments(bundle);
                                ((MainActivity) getActivity()).switchFragment(newdetails, true);
                            }
                        }).start();
                    }
                });

                // cancel the add
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });

                final AlertDialog dialog = builder.create();
                dialog.show();

                // disable the add button until input conditions are met
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

                // check user input
                inputChecking(name, dialog);
            }
        });
    }

    /**
     * inputChecking checks the user input when adding a new medication, the add button is disabled
     * until all format conditions are met.
     *
     * @param et is the medication name, which must not be empty.
     * @param d  is the new medication alertdialog.
     */
    public void inputChecking(EditText et, AlertDialog d) {

        final EditText name = et;
        final AlertDialog dialog = d;

        // ensure medication name is valid
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (name.getText().length() == 0) { // empty name
                    name.setError("Name cannot be empty"); // show error message
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                } else { // valid name
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    /**
     * Returns the current app context for use
     * in send/cancel notification functions, which are static
     */
    public static Context getAppContext() {
        return (Context) mContext;
    }

    /**
     * sendNotification runs every time the user changes anything in the reminders section of
     * an individual medicine. It gets the information submitted by the user about the medicine -
     * dosage, drug, when to take it, etc., and sends it to the notification creator, then uses the AlarmManager
     * to schedule it at the appropriate time to remind them to take said medicine.
     */
    public static void sendNotification(MedicineEntity medicine) {

        String remtime = medicine.getTime();
        String remdate = medicine.getDate();

        final String name = medicine.getTitle();

        if (medicine.getReminders()) {

            // Time variables
            int hourToSet = Integer.parseInt(remtime.substring(0, 2));
            int minuteToSet = Integer.parseInt(remtime.substring(3, 5));

            // Date variables
            int yearToSet = Integer.parseInt(remdate.substring(6, 10));
            int monthToSet = Integer.parseInt(remdate.substring(3, 5));
            int dayToSet = Integer.parseInt(remdate.substring(0, 2));

            AlarmManager alarmManager = (AlarmManager) getAppContext().getSystemService(Context.ALARM_SERVICE);

            Intent intentAlarm = new Intent(getAppContext(), AlarmReceiver.class);
            // Send the type of notification, name of the medicine and whether notification should be descriptive to AlarmReceiver
            intentAlarm.putExtra("type", "medicine");
            intentAlarm.putExtra("medicine", name);
            intentAlarm.putExtra("descriptive", medicine.getReminder_type());
            PendingIntent notifySender = PendingIntent.getBroadcast(getAppContext(), medicine.getUid(), intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);

            // Set notification to launch at medicine reminder time
            Calendar calendar = Calendar.getInstance();
            Calendar timeNow = Calendar.getInstance();
            calendar.set(yearToSet, monthToSet, dayToSet, hourToSet, minuteToSet, 0);
            // Subtract one from month to account for Java calendar
            calendar.add(Calendar.MONTH, -1);

            if (medicine.isDaily()) {
                // If date (not time) is in the past
                if (TimeUnit.MILLISECONDS.toDays(Math.abs(timeNow.getTimeInMillis() - calendar.getTimeInMillis())) < 0) {
                    // Sets date to today
                    calendar = Calendar.getInstance();
                } else {
                    // If time is in the past
                    if (calendar.compareTo(timeNow) != 1) {
                        calendar.add(Calendar.DAY_OF_YEAR, 1);
                    }
                }
                // Repeat every day
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, notifySender);
            } else {
                // If date (not time) is in the past
                if (TimeUnit.MILLISECONDS.toDays(Math.abs(timeNow.getTimeInMillis() - calendar.getTimeInMillis())) < 0) {
                    // If days between current date and past date divisible by 2,
                    if (TimeUnit.MILLISECONDS.toDays(Math.abs(timeNow.getTimeInMillis() - calendar.getTimeInMillis())) % 2 == 0) {
                        // Sets date to today
                        calendar = Calendar.getInstance();
                    } else {
                        // Sets date to tomorrow
                        calendar = Calendar.getInstance();
                        calendar.add(Calendar.DAY_OF_YEAR, 1);
                    }
                } else {
                    // If time is in the past
                    if (calendar.compareTo(timeNow) != 1) {
                        calendar.add(Calendar.DAY_OF_YEAR, 1);
                    }
                }
                // Else repeat every other day
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 2, notifySender);
            }
        }
    }

    /**
     * cancelNotification is called when the user switches off reminders altogether or specifically requests only
     * to be reminded at certain times. It cancels all notifications that have already been scheduled by the AlarmManager
     * that are no longer required.
     */
    public static void cancelNotification(MedicineEntity medicine) {
        Intent intent = new Intent(getAppContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getAppContext(), medicine.getUid(), intent, 0);
        AlarmManager alarmManager = (AlarmManager) getAppContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    public static void resetNotifications() {

        final Activity activity = (Activity) mContext;

        // Create a callable object for database transactions
        Callable callable = new Callable() {
            @Override
            public Object call() throws Exception {
                return AppDatabase.getAppDatabase(activity).appointmentsDao().getAll();
            }
        };

        // Get a Future object of all the appointment titles
        ExecutorService service = Executors.newFixedThreadPool(2);
        Future<List<MedicineEntity>> result = service.submit(callable);

        // Create a list of the appointment names
        List<MedicineEntity> medicines = null;
        try {
            medicines = result.get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (medicines != null) {
            for (MedicineEntity md : medicines)
                sendNotification(md);
        }
    }
}
