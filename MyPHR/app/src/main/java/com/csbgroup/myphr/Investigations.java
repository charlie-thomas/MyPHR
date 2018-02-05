package com.csbgroup.myphr;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.csbgroup.myphr.database.AppDatabase;
import com.csbgroup.myphr.database.InvestigationsEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

        DateAdapter adapter = new DateAdapter(getActivity(), getInvestigations());
        ListView listView = rootView.findViewById(R.id.investigations_list);
        listView.setAdapter(adapter);

        return rootView;
    }

    public List<CalendarEvent> getInvestigations() {

        // Create a callable object for database transactions
        Callable callable = new Callable() {
            @Override
            public Object call() throws Exception {
                return AppDatabase.getAppDatabase(getActivity()).investigationDao().getAll();
            }
        };

        // Get a Future object of all the appointment names
        ExecutorService service = Executors.newFixedThreadPool(2);
        Future<List<InvestigationsEntity>> result = service.submit(callable);

        // Create a list of the appointment names
        List<InvestigationsEntity> investigations = null;
        try {
            investigations = result.get();
        } catch (Exception e) {}


        // Convert into CalendarEvent objects
        ArrayList<CalendarEvent> events = new ArrayList<>();

        if (investigations != null) {
            for (InvestigationsEntity ie : investigations)
                events.add(new CalendarEvent(ie.getUid(), null, ie.getDate(), ie.getTitle() ,null));
        }

        return events;
    }
}
