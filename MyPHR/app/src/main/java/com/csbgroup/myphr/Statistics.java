package com.csbgroup.myphr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

        List<String> statistics = new ArrayList<String>(){{
            add("Height Velocity");
            add("Weight");
            add("BMI");
            add("Blood Pressure");
        }};

        ArrayAdapter<String> statisticsAdapter = new ArrayAdapter<>(
                getActivity(),
                R.layout.simple_list_item,
                statistics);

        ListView listView = rootView.findViewById(R.id.statistics_list );
        listView.setAdapter(statisticsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment details = StatisticsDetails.newInstance();

                // Create a bundle to pass the medicine name to the details fragment
                Bundle bundle = new Bundle();
                bundle.putString("title", parent.getAdapter().getItem(position).toString());
                details.setArguments(bundle);

                ((MainActivity) getActivity()).switchFragment(details);
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.settings, menu);
    }
}