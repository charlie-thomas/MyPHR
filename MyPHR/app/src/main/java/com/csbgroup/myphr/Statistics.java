package com.csbgroup.myphr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class Statistics extends Fragment {

    public Statistics() {
        // Required empty public constructor
    }

    public static Statistics newInstance() {
        Statistics fragment = new Statistics();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_statistics, container, false);

        ((MainActivity) getActivity()).setToolbar("My Statistics");
        setHasOptionsMenu(true);

        List<String> contacts = new ArrayList<String>(){{
            add("Body Mass Index (BMI)"); add("Blood Pressure"); add("Height Velocity"); add("Weight");}};

        ArrayAdapter<String> contactsAdapter = new ArrayAdapter<>(
                getActivity(),
                R.layout.simple_list_item,
                contacts);

        ListView listView = rootView.findViewById(R.id.statistics_list);
        listView.setAdapter(contactsAdapter);

        return rootView;
    }

}
