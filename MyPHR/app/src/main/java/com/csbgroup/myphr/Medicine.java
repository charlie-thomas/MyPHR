package com.csbgroup.myphr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

        ((MainActivity) getActivity()).setToolbar("My Medicine");
        setHasOptionsMenu(true);

        String[] meds = {"Growth Hormone", "Oestrogen", "Progesterone", "Thyroxine"};
        List<String> medicines = new ArrayList<>(Arrays.asList(meds));

        ArrayAdapter<String> medicineAdapter = new ArrayAdapter<>(
                getActivity(),
                R.layout.simple_list_item,
                medicines);

        ListView listView = rootView.findViewById(R.id.medicine_list);
        listView.setAdapter(medicineAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment details = MedicineDetails.newInstance();

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            ((MainActivity) getActivity()).switchFragment(MedicineSettings.newInstance());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
