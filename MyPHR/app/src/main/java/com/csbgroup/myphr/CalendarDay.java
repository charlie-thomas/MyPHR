package com.csbgroup.myphr;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

        // Create a grid with two columns (time and events)
        RecyclerView recyclerView = rootView.findViewById(R.id.calendar_grid);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(new NumberedAdapter(30));

        // Get the date passed from the Calendar fragment
        Bundle args = getArguments();
        Log.d("Date", args.getInt("day") + "/" + args.getInt("month") + "/" + args.getInt("year"));

        return rootView;
    }

}
