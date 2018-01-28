package com.csbgroup.myphr;


import android.app.AlertDialog;
import android.support.v4.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

    private FloatingActionButton fab; // the add investigation fab

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

        // view set up
        View rootView = inflater.inflate(R.layout.fragment_investigations, container, false);

        // display the investigations in list
        InvestigationsAdapter adapter = new InvestigationsAdapter(getActivity(), getInvestigations());
        ListView listView = rootView.findViewById(R.id.investigations_list);
        listView.setAdapter(adapter);

        // fab action for adding investigation
        fab = rootView.findViewById(R.id.investigation_fab);
        buildDialog(fab);

        return rootView;
    }

    /**
     * getInvestigations fetches the list of investigations from the database
     * @return the list of investigations
     */
    public ArrayList<InvestigationEvent> getInvestigations() {

        // Create a callable object for database transactions
        Callable callable = new Callable() {
            @Override
            public Object call() throws Exception {
                return AppDatabase.getAppDatabase(getActivity()).investigationDao().getAll();
            }
        };

        // Get a Future object of all the investigation titles
        ExecutorService service = Executors.newFixedThreadPool(2);
        Future<List<InvestigationsEntity>> result = service.submit(callable);

        // Create a list of the investigation titles
        List<InvestigationsEntity> investigations = null;
        try {
            investigations = result.get();
        } catch (Exception e) {}


        // Convert into InvestigationEvent objects
        ArrayList<InvestigationEvent> events = new ArrayList<>();

        if (investigations != null) {
            for (InvestigationsEntity ie : investigations)
                events.add(new InvestigationEvent(ie.getTitle(), ie.getDate(), ie.getNotes()));
        }

        return events;
    }

    /**
     * buildDialog builds the pop-up dialog for adding a new investigation
     * @param fab the floating action button which pulls up the dialog
     */
    public void buildDialog(FloatingActionButton fab) {

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // set up the dialog
                LayoutInflater inflater = getActivity().getLayoutInflater(); // get inflater
                View v = inflater.inflate(R.layout.add_investigation_dialog, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setView(v);

                // fetch the input values
                final EditText title = v.findViewById(R.id.inv_title);
                final EditText date = v.findViewById(R.id.inv_date);
                final EditText notes = v.findViewById(R.id.inv_notes);

                // add a new investigation action
                builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                // add the new investigation to the database

                            }
                        }).start();

                        // update the list view
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.detach(Investigations.this).attach(Investigations.this).commit();
                    }
                    // TODO: redirect to details fragment
                });

                // action for cancelling activity
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int arg1) {
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }
}
