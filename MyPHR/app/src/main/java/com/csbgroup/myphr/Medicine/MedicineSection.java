package com.csbgroup.myphr.Medicine;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

import com.csbgroup.myphr.MainActivity;
import com.csbgroup.myphr.R;

public class MedicineSection extends Fragment {

    public MedicineSection() {} // Required empty public constructor

    public static MedicineSection newInstance() {
        MedicineSection fragment = new MedicineSection();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // set up the view
        View rootView =  inflater.inflate(R.layout.fragment_medicine_section, container, false);
        ((MainActivity) getActivity()).setToolbar("My Medication", false);
        setHasOptionsMenu(true);

        // Set up TabHost
        TabHost mTabHost = rootView.findViewById(R.id.tabHost);
        mTabHost.setup();

        // Add first tab for the calendar
        TabHost.TabSpec mSpec = mTabHost.newTabSpec("Medication");
        mSpec.setContent(R.id.medicine_tab);
        mSpec.setIndicator("Medication");
        mTabHost.addTab(mSpec);

        // Add second tab for the investigations
        mSpec = mTabHost.newTabSpec("Holiday Letter");
        mSpec.setContent(R.id.holiday_tab);
        mSpec.setIndicator("Holiday Letter");
        mTabHost.addTab(mSpec);

        // Add content to tabs
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.medicine_tab, Medicine.newInstance());
        transaction.replace(R.id.holiday_tab, HolidayLetter.newInstance());
        transaction.commit();

        return rootView;
    }
}
