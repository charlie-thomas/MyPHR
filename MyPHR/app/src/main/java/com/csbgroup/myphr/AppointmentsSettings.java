package com.csbgroup.myphr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class AppointmentsSettings extends Fragment {

    public AppointmentsSettings() {
        // Required empty public constructor
    }

    public static AppointmentsSettings newInstance() {
        AppointmentsSettings fragment = new AppointmentsSettings();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_appointments_settings, container, false);

        ((MainActivity) getActivity()).setToolbar("Appointments Settings", true);
        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /* Navigation from settings fragment back to Appointments */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ((MainActivity) getActivity()).switchFragment(Appointments.newInstance());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}