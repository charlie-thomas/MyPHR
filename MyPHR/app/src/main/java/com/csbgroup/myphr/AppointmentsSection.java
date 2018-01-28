package com.csbgroup.myphr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

public class AppointmentsSection extends Fragment {

    public AppointmentsSection() {
        // Required empty public constructor
    }

    public static AppointmentsSection newInstance() {
        AppointmentsSection fragment = new AppointmentsSection();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_appointments_section, container, false);

        ((MainActivity) getActivity()).setToolbar("My Appointments", false);
        setHasOptionsMenu(true);

        // Set up TabHost
        TabHost mTabHost = rootView.findViewById(R.id.tabHost);
        mTabHost.setup();

        // Add first tab for the calendar
        TabHost.TabSpec mSpec = mTabHost.newTabSpec("Appointments");
        mSpec.setContent(R.id.appointments_tab);
        mSpec.setIndicator("Appointments");
        mTabHost.addTab(mSpec);

        // Add second tab for the investigations
        mSpec = mTabHost.newTabSpec("Investigations");
        mSpec.setContent(R.id.investigations_tab);
        mSpec.setIndicator("Investigations");
        mTabHost.addTab(mSpec);

        // Add content to tabs
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.appointments_tab, Appointments.newInstance());
        transaction.replace(R.id.investigations_tab, Investigations.newInstance());
        transaction.commit();

        return rootView;
    }

}