package com.csbgroup.myphr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

public class MedicineSettings extends Fragment {

    public MedicineSettings() {
        // Required empty public constructor
    }

    public static MedicineSettings newInstance() {
        MedicineSettings fragment = new MedicineSettings();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_medicine_settings, container, false);

        ((MainActivity) getActivity()).setToolbar("Medicines Settings", true);
        setHasOptionsMenu(false);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

}