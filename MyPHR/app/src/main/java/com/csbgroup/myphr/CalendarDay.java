package com.csbgroup.myphr;


import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.csbgroup.myphr.database.AppDatabase;
import com.csbgroup.myphr.database.AppointmentsEntity;
import com.csbgroup.myphr.database.MedicineEntity;
import com.csbgroup.myphr.database.SickDaysEntity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class CalendarDay extends Fragment {

    private String dateString;
    private Snackbar sb;

    public CalendarDay() {
        // Required empty public constructor
    }

    public static CalendarDay newInstance() {
        CalendarDay fragment = new CalendarDay();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView =  inflater.inflate(R.layout.fragment_calendar_day, container, false);

        // Get the date passed from the CalendarMonth fragment
        Bundle args = getArguments();
        TextView dateTitle = rootView.findViewById(R.id.date);
        dateString = args.getString("date");
        dateTitle.setText(dateString);

        ImageButton prevDate = rootView.findViewById(R.id.previous_date);
        prevDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                try {
                    switchDate(changeDate(-1));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        ImageButton nextDate = rootView.findViewById(R.id.next_date);
        nextDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                try {
                    switchDate(changeDate(1));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });


        List<CalendarEvent> daysEvents = Collections.emptyList();
        try {
            daysEvents = getEvents(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        List<List<CalendarEvent>> hours = new ArrayList<>();
        for (int i = 0; i < 24; i++) {

            List<CalendarEvent> hourEvents = new ArrayList<>();

            for (CalendarEvent ce : daysEvents) {
                if (i == Integer.parseInt(ce.getTime().substring(0, 2))) {
                    hourEvents.add(new CalendarEvent(ce.getUid(), i, ce.getTime(), dateString, ce.getEvent(), ce.getType()));
                }
            }

            if (hourEvents.size() == 0) hourEvents.add(new CalendarEvent(0, i, null, dateString, null, "Empty"));

            hours.add(hourEvents);
        }

        CalendarAdapter adapter = new CalendarAdapter(hours);
        RecyclerView calendarList = rootView.findViewById(R.id.calendar_list);
        calendarList.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        calendarList.addItemDecoration(new DividerItemDecoration(rootView.getContext(), DividerItemDecoration.VERTICAL));
        calendarList.setAdapter(adapter);

        calendarList.scrollToPosition(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));

        try {
            if (isSickDay(dateString)) {
                sb = sb.make(rootView.findViewById(R.id.viewSnack), dateString + " is marked as a sick day", Snackbar.LENGTH_INDEFINITE);
                sb.show();
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        // fab action for adding sick day
        FloatingActionButton fab = rootView.findViewById(R.id.sick_fab);
        buildDialog(fab, dateString);

        // back button
        ((MainActivity) getActivity()).setToolbar("", true);
        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onStop() {
        if (sb != null) sb.dismiss();
        super.onStop();
    }

    public String changeDate(int value) throws ParseException {

        try {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

            c.setTime(df.parse(dateString));

            c.add(Calendar.DATE, value);
            return df.format(c.getTime());

        } catch (Exception e) {
            return dateString;
        }
    }

    @Override
    public void setUserVisibleHint(boolean visible)
    {
        super.setUserVisibleHint(visible);
        if (!visible && sb != null ){
            sb.dismiss();
        }
    }

    public void switchDate(String newDate) {
        Fragment newDayFragment = CalendarDay.newInstance();

        Bundle bundle = new Bundle();
        bundle.putString("date", newDate);
        newDayFragment.setArguments(bundle);

        ((MainActivity) getActivity()).switchFragment(newDayFragment, true);
    }

    public List<CalendarEvent> getEvents(final String date) throws ParseException {
        List<CalendarEvent> all_events = new ArrayList<>();

        // Create a callable object to get appointments from database
        Callable callable_app = new Callable() {
            @Override
            public Object call() throws Exception {
                return AppDatabase.getAppDatabase(getActivity()).appointmentsDao().getAppointmentByDate(date);
            }
        };

        // Create a callable object to get medicine from database
        Callable callable_med = new Callable() {
            @Override
            public Object call() throws Exception {
                return AppDatabase.getAppDatabase(getActivity()).medicineDao().getAll();
            }
        };

        // Get a Future object of all the appointment names
        ExecutorService service = Executors.newFixedThreadPool(2);
        Future<List<AppointmentsEntity>> result_app = service.submit(callable_app);
        Future<List<MedicineEntity>> result_med = service.submit(callable_med);

        // Create lists of the appointment and medicine names
        List<AppointmentsEntity> appointments = Collections.emptyList();
        List<MedicineEntity> medicines = Collections.emptyList();
        try {
            appointments = result_app.get();
            medicines = result_med.get();
        } catch (Exception e) {}

        for (AppointmentsEntity ae : appointments)
            all_events.add(new CalendarEvent(ae.getUid(), 0, ae.getTime(), ae.getDate(), ae.getTitle(), "Appointment"));

        for (MedicineEntity me : medicines) {
            if (me.getReminders() && (me.isDaily() || (me.isOther_days() && isOtherDay(me.getDate(), date))))
                all_events.add(new CalendarEvent(me.getUid(), 0, me.getTime(), me.getDate(), me.getTitle(), "Medicine"));
        }

        return all_events;
    }

    /* Helper function to calculate whether the start date and follow up date are correctly spaced */
    public static boolean isOtherDay(String d1, String d2) throws ParseException {
        DateFormat f = new SimpleDateFormat("dd/MM/yyyy");
        long diff = f.parse(d2).getTime() - f.parse(d1).getTime();

        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) % 2 == 0;
    }

    public boolean isSickDay(final String date) throws ExecutionException, InterruptedException {

        // Create a callable object to get sick day from database
        Callable callable = new Callable() {
            @Override
            public Object call() throws Exception {
                return AppDatabase.getAppDatabase(getActivity()).sickDaysDao().getSickDaysByDate(date);
            }
        };

        // Get a Future object of the sick day
        ExecutorService service = Executors.newFixedThreadPool(2);
        Future<SickDaysEntity> result_app = service.submit(callable);

        return result_app.get() != null;
    }

    /**
     * Provides navigation for menu items; currenty only needed for navigation back to the
     * main calendar view.
     * @param item the clicked menu item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // back button
                ((MainActivity) getActivity()).switchFragment(CalendarMonth.newInstance(), false);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * buildDialog builds the pop-up dialog for marking a sick day
     * @param fab the floating action button which pulls up the dialog
     */
    public void buildDialog(FloatingActionButton fab, final String date) {

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // set up the dialog
                LayoutInflater inflater = getActivity().getLayoutInflater(); // get inflater
                View v = inflater.inflate(R.layout.add_sick_day_dialog, null);
                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
                builder.setView(v);

                // add new sick day into database
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        try {
                            if (!isSickDay(date)) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AppDatabase db = AppDatabase.getAppDatabase(getActivity());
                                        db.sickDaysDao().insert(new SickDaysEntity(date));
                                    }
                                }).start();

                                // Refresh
                                Fragment cd = CalendarDay.newInstance();
                                Bundle bundle = new Bundle();
                                bundle.putString("date", date);
                                cd.setArguments(bundle);
                                ((MainActivity)getActivity()).switchFragment(cd, true);
                            }
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });

                // action for cancelling add
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        try {
                            if (isSickDay(date)) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AppDatabase db = AppDatabase.getAppDatabase(getActivity());
                                        db.sickDaysDao().delete(db.sickDaysDao().getSickDaysByDate(date));
                                    }
                                }).start();

                                // Refresh
                                Fragment cd = CalendarDay.newInstance();
                                Bundle bundle = new Bundle();
                                bundle.putString("date", date);
                                cd.setArguments(bundle);
                                ((MainActivity)getActivity()).switchFragment(cd, true);
                            }
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });

                android.app.AlertDialog dialog = builder.create();
                dialog.show();

                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.colorSick));
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.colorSick));
            }
        });
    }
}
