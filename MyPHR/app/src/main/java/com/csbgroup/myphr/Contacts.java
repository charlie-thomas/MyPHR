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

import java.util.List;
import java.util.ArrayList;

public class Contacts extends Fragment {

    public Contacts() {
        // Required empty public constructor
    }

    public static Contacts newInstance() {
        Contacts fragment = new Contacts();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_contacts, container, false);

        ((MainActivity) getActivity()).setToolbar("My Contacts", false);
        setHasOptionsMenu(true);

        List<String> contacts = new ArrayList<String>(){{
            add("Dr. A"); add("Dr. B"); add("Dr. C"); add("Ms. D"); add("Mr. E");}};

        ArrayAdapter<String> contactsAdapter = new ArrayAdapter<>(
                getActivity(),
                R.layout.simple_list_item,
                contacts);

        ListView listView = rootView.findViewById(R.id.contacts_list);
        listView.setAdapter(contactsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment details = ContactDetails.newInstance();

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
