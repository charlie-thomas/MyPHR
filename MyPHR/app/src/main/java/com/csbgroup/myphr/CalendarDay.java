package com.csbgroup.myphr;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;

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

        // Get the date passed from the Calendar fragment
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

        ArrayList<CalendarEvent> events = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            String event = null;
            if (i == 8) event = "Appointment";
            events.add(new CalendarEvent(i+":00", null, event));
        }

        CalendarAdapter adapter = new CalendarAdapter(getActivity(), events);

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
}
