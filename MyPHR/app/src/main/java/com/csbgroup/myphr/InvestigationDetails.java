package com.csbgroup.myphr;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.csbgroup.myphr.database.AppDatabase;
import com.csbgroup.myphr.database.InvestigationsEntity;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class InvestigationDetails extends Fragment {

    private InvestigationsEntity thisinvestigation; // the appointment we're viewing now

    private Menu editMenu;
    private String mode = "view";
    private View rootView;

    private KeyListener titlelistener, datelistener, noteslistener;
    private Drawable titlebackground, datebackground, notesbackground;

    public InvestigationDetails() {
        // Required empty public constructor
    }

    public static InvestigationDetails newInstance() {
        InvestigationDetails fragment = new InvestigationDetails();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_investigation_details, container, false);

        // fill in the values

        Bundle args = getArguments();
        InvestigationsEntity investigation = getInvestigation(Integer.parseInt(args.getString("uid")));
        this.thisinvestigation = investigation;

        EditText title = rootView.findViewById(R.id.investigation_name);
        title.setText(investigation.getTitle());
        titlelistener = title.getKeyListener();
        titlebackground = title.getBackground();
        title.setKeyListener(null);
        title.setBackground(null);

        EditText date = rootView.findViewById(R.id.investigation_date);
        date.setText(investigation.getDate());
        datelistener = date.getKeyListener();
        datebackground = date.getBackground();
        date.setBackground(null);
        date.setKeyListener(null);

        EditText notes = rootView.findViewById(R.id.notes);
        notes.setText(investigation.getNotes());
        noteslistener = notes.getKeyListener();
        notesbackground = notes.getBackground();
        notes.setBackground(null);
        notes.setKeyListener(null);

        // back button
        ((MainActivity) getActivity()).setToolbar("My Appointments", true);
        setHasOptionsMenu(true);

        return rootView;
    }

    /**
     * Fetches a single appointment from the database.
     * @param uid is the primary key of the appointment to be retrieved
     * @return the appointment entity
     */
    private InvestigationsEntity getInvestigation(final int uid) {

        // Create a callable object for database transactions
        Callable callable = new Callable() {
            @Override
            public Object call() throws Exception {
                return AppDatabase.getAppDatabase(getActivity()).investigationDao().getInvestigation(uid);
            }
        };

        ExecutorService service = Executors.newFixedThreadPool(2);
        Future<InvestigationsEntity> result = service.submit(callable);

        InvestigationsEntity investigation = null;
        try {
            investigation = result.get();
        } catch (Exception e) {}

        return investigation;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.edit, menu);
        editMenu = menu;
    }

    /**
     * Provides navigation/actions for menu items.
     * @param item is the clicked menu item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // back button - go back
                ((MainActivity) getActivity()).switchFragment(AppointmentsSection.newInstance());
                return true;

            case R.id.details_edit: // edit button - edit appointment details
                switchMode();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * switchMode toggles between viewing and editing the appointment details.
     */
    public void switchMode() {

        if (this.mode.equals("view")) {
            editMenu.getItem(0).setIcon(R.drawable.tick);

            EditText title = rootView.findViewById(R.id.investigation_name);
            title.setText(thisinvestigation.getTitle());
            title.setBackground(titlebackground);
            title.setKeyListener(titlelistener);

            EditText date = rootView.findViewById(R.id.investigation_date);
            date.setText(thisinvestigation.getDate());
            date.setKeyListener(datelistener);
            date.setBackground(datebackground);

            EditText notes = rootView.findViewById(R.id.notes);
            notes.setText(thisinvestigation.getNotes());
            notes.setKeyListener(noteslistener);
            notes.setBackground(notesbackground);

            //TODO: make delete button appear

            this.mode = "edit";
            return;
        }

        if (this.mode.equals("edit")){
            editMenu.getItem(0).setIcon(R.drawable.edit);

            final EditText title = rootView.findViewById(R.id.investigation_name);
            title.setKeyListener(null);
            title.setBackground(null);

            final EditText date = rootView.findViewById(R.id.investigation_date);
            date.setKeyListener(null);
            date.setBackground(null);

            final EditText notes = rootView.findViewById(R.id.notes);
            notes.setKeyListener(null);
            notes.setBackground(null);

            // update the contact in the database
            new Thread(new Runnable() {
                @Override
                public void run() {
                    AppDatabase db = AppDatabase.getAppDatabase(getActivity());
                    thisinvestigation.setTitle(title.getText().toString());
                    thisinvestigation.setDate(date.getText().toString());
                    thisinvestigation.setNotes(notes.getText().toString());
                    db.investigationDao().update(thisinvestigation);

                    // refresh to get rid of keyboard
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.detach(InvestigationDetails.this).attach(InvestigationDetails.this).commit();
                }
            }).start();

            this.mode = "view";
        }
    }

}