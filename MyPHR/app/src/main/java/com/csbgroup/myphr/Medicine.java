package com.csbgroup.myphr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Medicine extends Fragment {

    public Medicine() {
        // Required empty public constructor
    }

    public static Medicine newInstance() {
        Medicine fragment = new Medicine();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_medicine, container, false);

        List<String> medicines = new ArrayList<>();
        for (int i = 1; i < 10; i++) medicines.add("Medicine " + i);

        ArrayAdapter<String> medicineAdapter = new ArrayAdapter<>(
                getActivity(),
                R.layout.simple_list_item,
                medicines);

        ListView listView = rootView.findViewById(R.id.medicine_list);
        listView.setAdapter(medicineAdapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

}
