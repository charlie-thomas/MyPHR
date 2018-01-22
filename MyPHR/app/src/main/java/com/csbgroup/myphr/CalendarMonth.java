package com.csbgroup.myphr;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

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

        return rootView;
    }

}
