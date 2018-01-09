package com.csbgroup.myphr;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class CalendarDay extends Fragment {


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

        ArrayList<CalendarEvent> events = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            String event = null;
            if (i == 8) event = "Appointment";
            events.add(new CalendarEvent(i+":00", null, event));
        }

        CalendarAdapter adapter = new CalendarAdapter(getActivity(), events);

        ListView calendarList = rootView.findViewById(R.id.calendar_list);
        calendarList.setAdapter(adapter);


        // Get the date passed from the Calendar fragment
        Bundle args = getArguments();
        TextView dateTitle = rootView.findViewById(R.id.date);
        String dateString = args.getInt("day") + "/" + args.getInt("month") + "/" + args.getInt("year");
        dateTitle.setText(dateString);

        return rootView;
    }

}
