package com.csbgroup.myphr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
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

        ((MainActivity) getActivity()).setToolbar("Clinic - Dr Smith");
        setHasOptionsMenu(false);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

}