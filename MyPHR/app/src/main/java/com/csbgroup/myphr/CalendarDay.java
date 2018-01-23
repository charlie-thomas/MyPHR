package com.csbgroup.myphr;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.csbgroup.myphr.database.AppDatabase;
import com.csbgroup.myphr.database.AppointmentsEntity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CalendarDay extends Fragment {

    private String dateString;

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

        View rootView =  inflater.inflate(R.layout.fragment_calendar_day, container, false);

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


        List<AppointmentsEntity> daysEvents = getEvents(dateString);

        ArrayList<CalendarEvent> hours = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            String event = null;

            if (daysEvents != null) {
                for (AppointmentsEntity de : daysEvents) {
                    if (String.valueOf(i).equals(de.getTime())) event = de.getTitle();
                }
            }

            hours.add(new CalendarEvent(i+":00", dateString, event));
        }

        CalendarAdapter adapter = new CalendarAdapter(getActivity(), hours);

        ListView calendarList = rootView.findViewById(R.id.calendar_list);
        calendarList.setAdapter(adapter);

        return rootView;
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

    public void switchDate(String newDate) {
        Fragment newDayFragment = CalendarDay.newInstance();

        Bundle bundle = new Bundle();
        bundle.putString("date", newDate);
        newDayFragment.setArguments(bundle);

        ((MainActivity) getActivity()).switchFragment(newDayFragment);
    }

    public List<AppointmentsEntity> getEvents(final String date) {

        // Create a callable object for database transactions
        Callable callable = new Callable() {
            @Override
            public Object call() throws Exception {
                return AppDatabase.getAppDatabase(getActivity()).appointmentsDao().getAppointmentByDate(date);
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

        return appointments;
    }
}
