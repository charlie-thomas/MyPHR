package com.csbgroup.myphr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

        ((MainActivity) getActivity()).setToolbar("My Calendar");
        setHasOptionsMenu(false);

        return rootView;
    }

}
