package com.csbgroup.myphr.Medicine;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.csbgroup.myphr.R;

/**
 * the HolidayLetter class is the fragment displaying the holiday travel letter
 */
public class HolidayLetter extends Fragment {

    public HolidayLetter() {} // Required empty public constructor

    public static HolidayLetter newInstance() {
        HolidayLetter fragment = new HolidayLetter();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // display the holiday letter
        View rootView = inflater.inflate(R.layout.fragment_holiday_letter, container, false);

        return rootView;
    }
}