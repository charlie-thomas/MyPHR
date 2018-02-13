package com.csbgroup.myphr;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.csbgroup.myphr.database.AppDatabase;
import com.csbgroup.myphr.database.AppointmentsEntity;
import com.csbgroup.myphr.database.MedicineEntity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CalendarMonth extends Fragment {

    public CalendarMonth() {
        // Required empty public constructor
    }

    public static CalendarMonth newInstance() {
        CalendarMonth fragment = new CalendarMonth();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_calendar_month, container, false);

        ((MainActivity) getActivity()).setToolbar("My Calendar", false);
        setHasOptionsMenu(false);

        CalendarView calendarView = rootView.findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            /* Creates a bundle containing the date selected from the CalendarView, and passes this
             * date through to the new day view fragment, which the app switches to */
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                Fragment dayFragment = CalendarDay.newInstance();

                String day = String.valueOf(dayOfMonth);
                if (dayOfMonth < 10) day = "0" + dayOfMonth;

                Log.d("month", String.valueOf(month + 1));
                String month_ = String.valueOf(month + 1);
                if ((month + 1) < 10) month_ = "0" + month_;

                // Create a bundle to pass the selected date to the day view fragment
                Bundle bundle = new Bundle();
                bundle.putString("date", day + "/" + month_ + "/" + year);
                dayFragment.setArguments(bundle);

                ((MainActivity) getActivity()).switchFragment(dayFragment);
            }
        });

        // Next appointment
        final AppointmentsEntity upcoming_appointment = getUpcomingAppointment();

        if (upcoming_appointment == null) return rootView;

        LinearLayout upcoming_ll = rootView.findViewById(R.id.upcoming_layout);
        TextView upcomingDate = rootView.findViewById(R.id.upcoming_date);
        TextView upcomingApp = rootView.findViewById(R.id.upcoming_app_name);
        upcomingDate.setText(upcoming_appointment.getDate());
        upcomingApp.setText(upcoming_appointment.getTitle());

        upcoming_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment eventFrag = AppointmentsDetails.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("uid", String.valueOf(upcoming_appointment.getUid()));
                eventFrag.setArguments(bundle);

                ((MainActivity) getContext()).switchFragment(eventFrag);
            }
        });

        // Today's Medicines
        LinearLayout todays_meds = rootView.findViewById(R.id.todays_meds);
        try {
            for (CalendarEvent med : getTodaysMedicine()) {
                final CalendarEvent _med = med;

                LinearLayout ll_med = (LinearLayout) inflater.inflate(R.layout.todays_meds_list_item, null);
                ll_med.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccentDark));

                TextView time_med = ll_med.findViewById(R.id.upcoming_time_med);
                time_med.setText(med.getTime());

                TextView event_med = ll_med.findViewById(R.id.upcoming_med_name);
                event_med.setText(med.getEvent());

                Log.d("MED", ""+_med.getUid());
                ll_med.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Fragment eventFrag = MedicineDetails.newInstance();
                        Bundle bundle = new Bundle();
                        bundle.putString("uid", String.valueOf(_med.getUid()));
                        eventFrag.setArguments(bundle);

                        ((MainActivity) getContext()).switchFragment(eventFrag);
                    }
                });

                todays_meds.addView(ll_med);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return rootView;
    }

    public AppointmentsEntity getUpcomingAppointment() {

        // Create a callable object for database transactions
        Callable callable = new Callable() {
            @Override
            public Object call() throws Exception {
                return AppDatabase.getAppDatabase(getActivity()).appointmentsDao().getAll();
            }
        };

        // Get a Future object of all the appointment names
        ExecutorService service = Executors.newFixedThreadPool(2);
        Future<List<AppointmentsEntity>> result = service.submit(callable);

        // Create a list of the appointment names
        List<AppointmentsEntity> appointments = null;
        try {
            appointments = result.get();
        } catch (Exception e) {}

        if (appointments == null) return null;

        Collections.sort(appointments, new Comparator<AppointmentsEntity>() {
            DateFormat f = new SimpleDateFormat("dd/MM/yyyy");

            @Override
            public int compare(AppointmentsEntity d1, AppointmentsEntity d2) {
                try {
                    return f.parse(d1.getDate()).compareTo(f.parse(d2.getDate()));
                } catch (Exception e) {
                }
                return 0;
            }
        });

        try {
            return appointments.get(0);
        } catch (Exception e) {
            return null;
        }
    }

    public List<CalendarEvent> getTodaysMedicine() throws ParseException {

        List<CalendarEvent> todays_meds = new ArrayList<>();

        // Create a callable object for database transactions
        Callable callable = new Callable() {
            @Override
            public Object call() throws Exception {
                return AppDatabase.getAppDatabase(getActivity()).medicineDao().getAll();
            }
        };

        // Get a Future object of all the medicine names
        ExecutorService service = Executors.newFixedThreadPool(2);
        Future<List<MedicineEntity>> result = service.submit(callable);

        // Create a list of the medicine names
        List<MedicineEntity> medicines = Collections.emptyList();
        try {
            medicines = result.get();
        } catch (Exception e) {}

        // Get today's date
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Calendar today = Calendar.getInstance();

        // Create CalendarEvents for the days medicines, and add them to the returning array
        for (MedicineEntity me : medicines) {
            if (me.isDaily() || (me.isOther_days() && CalendarDay.isOtherDay(me.getDate(), df.format(today.getTime()))))
                todays_meds.add(new CalendarEvent(me.getUid(), null, me.getTime(), me.getDate(), me.getTitle(), "Medicine"));
        }

        Collections.sort(todays_meds, new Comparator<CalendarEvent>() {
            @Override
            public int compare(CalendarEvent e1, CalendarEvent e2) {
                return e1.getTime().replace(":", "").compareTo(e2.getTime().replace(":", ""));
            }
        });

        return todays_meds;
    }
}
