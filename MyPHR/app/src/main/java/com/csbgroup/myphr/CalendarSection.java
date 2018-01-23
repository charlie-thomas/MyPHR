package com.csbgroup.myphr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

public class CalendarSection extends Fragment {

    public CalendarSection() {
        // Required empty public constructor
    }

    public static CalendarSection newInstance() {
        CalendarSection fragment = new CalendarSection();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_calendar, container, false);

        ((MainActivity) getActivity()).setToolbar("My Calendar", false);
        setHasOptionsMenu(false);

        // Set up TabHost
        TabHost mTabHost = rootView.findViewById(R.id.tabHost);
        mTabHost.setup();

        // Add first tab for the calendar
        TabHost.TabSpec mSpec = mTabHost.newTabSpec("Calendar");
        mSpec.setContent(R.id.calendar_tab);
        mSpec.setIndicator("Calendar");
        mTabHost.addTab(mSpec);

        // Add second tab for the investigations
        mSpec = mTabHost.newTabSpec("Investigations");
        mSpec.setContent(R.id.investigations_tab);
        mSpec.setIndicator("Investigations");
        mTabHost.addTab(mSpec);

        // Add content to tabs
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.calendar_tab, CalendarMonth.newInstance());
        transaction.replace(R.id.investigations_tab, Investigations.newInstance());
        transaction.commit();

        return rootView;
    }

}
