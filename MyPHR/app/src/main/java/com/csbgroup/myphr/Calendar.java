package com.csbgroup.myphr;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

public class Calendar extends Fragment {

    public Calendar() {
        // Required empty public constructor
    }

    public static Calendar newInstance() {
        Calendar fragment = new Calendar();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_calendar, container, false);

        ((MainActivity) getActivity()).setToolbar("My Calendar", false);
        setHasOptionsMenu(false);

        CalendarView calendarView = rootView.findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            /* Creates a bundle containing the date selected from the CalendarView, and passes this
             * date through to the new day view fragment, which the app switches to */
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                Fragment dayFragment = CalendarDay.newInstance();

                // Create a bundle to pass the selected date to the day view fragment
                Bundle bundle = new Bundle();
                bundle.putInt("year", year);
                bundle.putInt("month", month + 1);
                bundle.putInt("day", dayOfMonth);
                dayFragment.setArguments(bundle);

                ((MainActivity) getActivity()).switchFragment(dayFragment);
            }
        });

        return rootView;
    }

}
