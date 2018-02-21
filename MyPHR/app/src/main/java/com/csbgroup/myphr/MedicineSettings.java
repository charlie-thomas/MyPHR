package com.csbgroup.myphr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /* Navigation from settings fragment back to Medicine */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ((MainActivity) getActivity()).switchFragment(Medicine.newInstance());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}