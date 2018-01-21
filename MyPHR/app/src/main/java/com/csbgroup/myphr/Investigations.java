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

public class Investigations extends Fragment {

    public Investigations() {
        // Required empty public constructor
    }

    public static Investigations newInstance() {
        Investigations fragment = new Investigations();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_investigations, container, false);

        InvestigationsAdapter adapter = new InvestigationsAdapter(getActivity(), getInvestigations());
        ListView listView = rootView.findViewById(R.id.investigations_list);
        listView.setAdapter(adapter);

        return rootView;
    }

    public ArrayList<InvestigationEvent> getInvestigations() {
        ArrayList<InvestigationEvent> investigations = new ArrayList<>();

        investigations.add(new InvestigationEvent("Hearing Test", "12/03/2017"));
        investigations.add(new InvestigationEvent("Blood Test", "17/07/2017"));
        investigations.add(new InvestigationEvent("Blood Test", "05/01/2018"));

        return investigations;
    }
}
