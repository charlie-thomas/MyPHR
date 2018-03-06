package com.csbgroup.myphr;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.csbgroup.myphr.database.AppDatabase;
import com.csbgroup.myphr.database.AppointmentsEntity;
import com.csbgroup.myphr.database.InvestigationsEntity;
import com.csbgroup.myphr.database.MedicineEntity;
import com.csbgroup.myphr.database.SickDaysEntity;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.security.acl.LastOwnerException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static android.view.View.GONE;

public class CalendarMonth extends Fragment {

    private CalendarEvent upcomingAppointment;

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

        MaterialCalendarView calendarView = rootView.findViewById(R.id.calendarView);
        calendarView.setDateSelected(Calendar.getInstance().getTime(), true);
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull com.prolificinteractive.materialcalendarview.CalendarDay date, boolean selected) {
                Fragment dayFragment = CalendarDay.newInstance();

                String day = ""+date.getDay();
                if (date.getDay() < 10) day = "0" + day;

                String month = String.valueOf(date.getMonth() + 1);
                if ((date.getMonth() + 1) < 10) month = "0" + month;

                // Create a bundle to pass the selected date to the day view fragment
                Bundle bundle = new Bundle();
                bundle.putString("date", day + "/" + month + "/" + date.getYear());
                dayFragment.setArguments(bundle);

                ((MainActivity) getActivity()).switchFragment(dayFragment, true);
            }
        });

        List<CalendarEvent> all_events = Collections.emptyList();
        try {
            all_events = getAllEvents();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        DateFormat f = new SimpleDateFormat("dd/MM/yyyy");

        HashSet<com.prolificinteractive.materialcalendarview.CalendarDay> apps = new HashSet<>();
        HashSet<com.prolificinteractive.materialcalendarview.CalendarDay> invest = new HashSet<>();
        HashSet<com.prolificinteractive.materialcalendarview.CalendarDay> sickdays = new HashSet<>();

        for(CalendarEvent ce : all_events) {
            com.prolificinteractive.materialcalendarview.CalendarDay calendarDay = null;
            try {
                 calendarDay = com.prolificinteractive.materialcalendarview.CalendarDay.from(f.parse(ce.getDate()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            switch (ce.getType()) {
                case "Sick":
                    sickdays.add(calendarDay);
                    break;
                case "Appointment":
                    apps.add(calendarDay);
                    break;
                case "Investigation":
                    invest.add(calendarDay);
                    break;
            }
        }

        calendarView.addDecorator(new EventDecorator(ContextCompat.getColor(rootView.getContext(), R.color.colorAccent), apps));
        calendarView.addDecorator(new EventDecorator(ContextCompat.getColor(rootView.getContext(), R.color.colorAccentDark), invest));
        calendarView.addDecorator(new EventDecorator(ContextCompat.getColor(rootView.getContext(), R.color.colorSick), sickdays));

        LinearLayout upcoming_ll = rootView.findViewById(R.id.upcoming_layout);
        TextView upcomingDate = rootView.findViewById(R.id.upcoming_date);
        TextView upcomingApp = rootView.findViewById(R.id.upcoming_app_name);
        upcomingDate.setText(upcomingAppointment.getDate());
        upcomingApp.setText(upcomingAppointment.getEvent());

        if (!Objects.equals(upcomingAppointment.getEvent(), "No Upcoming Appointments")) {
            upcoming_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment eventFrag = AppointmentsDetails.newInstance();
                    Bundle bundle = new Bundle();
                    bundle.putString("uid", String.valueOf(upcomingAppointment.getUid()));
                    eventFrag.setArguments(bundle);

                    BottomNavigationView bn = getActivity().findViewById(R.id.bottom_nav);
                    bn.setSelectedItemId(R.id.appointments);
                    ((MainActivity) getContext()).switchFragment(eventFrag, true);
                }
            });
        } else {
            upcoming_ll.setVisibility(GONE);
            rootView.findViewById(R.id.upcoming_app).setVisibility(GONE);
        }

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

                ll_med.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Fragment eventFrag = MedicineDetails.newInstance();
                        Bundle bundle = new Bundle();
                        bundle.putString("uid", String.valueOf(_med.getUid()));
                        eventFrag.setArguments(bundle);

                        BottomNavigationView bn = getActivity().findViewById(R.id.bottom_nav);
                        bn.setSelectedItemId(R.id.medicine);
                        ((MainActivity) getContext()).switchFragment(eventFrag, true);
                    }
                });

                todays_meds.addView(ll_med);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (todays_meds.getChildCount() == 0) {
            todays_meds.setVisibility(View.GONE);
            rootView.findViewById(R.id.upcoming_med).setVisibility(View.GONE);
        }
        return rootView;
    }

    public CalendarEvent getUpcomingAppointment(List<CalendarEvent> appointments) throws ParseException {

        CalendarEvent placeholder = new CalendarEvent(0, 0,null, "", "No Upcoming Appointments", null);
        if (appointments == null) return placeholder;

        // Sort the appointments by date and then time
        final DateFormat f = new SimpleDateFormat("dd/MM/yyyy");
        Collections.sort(appointments, new Comparator<CalendarEvent>() {
            @Override
            public int compare(CalendarEvent e1, CalendarEvent e2) {
                int dateComp = 0;
                try {
                    dateComp = f.parse(e1.getDate()).compareTo(f.parse(e2.getDate()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (dateComp != 0) return dateComp;
                return e1.getTime().replace(":", "").compareTo(e2.getTime().replace(":", ""));
            }
        });

        // Pick the next upcoming appointment
        DateFormat f2 = new SimpleDateFormat("HHmm");
        Date today = Calendar.getInstance().getTime();
        for (CalendarEvent app : appointments) {
            int comp = f.parse(app.getDate()).compareTo(today);
            if (comp > 0 || (comp == 0 && Integer.parseInt(app.getTime().replace(":", "")) > Integer.parseInt(f2.format(today))))
                return app;
        }

        return placeholder;
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
            if (me.getReminders() && (me.isDaily() || (me.isOther_days() && CalendarDay.isOtherDay(me.getDate(), df.format(today.getTime())))))
                todays_meds.add(new CalendarEvent(me.getUid(), 0, me.getTime(), me.getDate(), me.getTitle(), "Medicine"));
        }

        Collections.sort(todays_meds, new Comparator<CalendarEvent>() {
            @Override
            public int compare(CalendarEvent e1, CalendarEvent e2) {
                return e1.getTime().replace(":", "").compareTo(e2.getTime().replace(":", ""));
            }
        });

        return todays_meds;
    }

    public List<CalendarEvent> getAllEvents() throws ParseException {

        List<CalendarEvent> all_events = new ArrayList<>();

        // Create a callable object to get appointments from database
        Callable callable_app = new Callable() {
            @Override
            public Object call() throws Exception {
                return AppDatabase.getAppDatabase(getActivity()).appointmentsDao().getAll();
            }
        };

        // Create a callable object to get investigations from database
        Callable callable_inv = new Callable() {
            @Override
            public Object call() throws Exception {
                return AppDatabase.getAppDatabase(getActivity()).investigationDao().getAll();
            }
        };

        // Create a callable object to get sick days from database
        Callable callable_sick = new Callable() {
            @Override
            public Object call() throws Exception {
                return AppDatabase.getAppDatabase(getActivity()).sickDaysDao().getAll();
            }
        };

        // Get a Future object of all the appointment names
        ExecutorService service = Executors.newFixedThreadPool(2);
        Future<List<AppointmentsEntity>> result_app = service.submit(callable_app);
        Future<List<InvestigationsEntity>> result_inv = service.submit(callable_inv);
        Future<List<SickDaysEntity>> result_sick = service.submit(callable_sick);

        // Create lists of the events
        List<AppointmentsEntity> appointments = Collections.emptyList();
        List<InvestigationsEntity> investigations = Collections.emptyList();
        List<SickDaysEntity> sickdays = Collections.emptyList();
        try {
            appointments = result_app.get();
            investigations = result_inv.get();
            sickdays = result_sick.get();
        } catch (Exception e) {}

        for (AppointmentsEntity ae : appointments)
            all_events.add(new CalendarEvent(ae.getUid(), 0, ae.getTime(), ae.getDate(), ae.getTitle(), "Appointment"));

        upcomingAppointment = getUpcomingAppointment(all_events);

        for (InvestigationsEntity ie : investigations)
            all_events.add(new CalendarEvent(ie.getUid(), 0, null, ie.getDate(), ie.getTitle(), "Investigation"));

        for(SickDaysEntity sd : sickdays)
            all_events.add(new CalendarEvent(sd.getUid(), 0, null, sd.getDate(), null, "Sick"));

        return all_events;
    }
}
