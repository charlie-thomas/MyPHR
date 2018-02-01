package com.csbgroup.myphr;

import android.app.AlertDialog;
import android.support.v4.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.csbgroup.myphr.database.AppDatabase;
import com.csbgroup.myphr.database.InvestigationsEntity;
import com.csbgroup.myphr.database.MedicineEntity;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
                final EditText day = v.findViewById(R.id.inv_DD);
                final EditText month = v.findViewById(R.id.inv_MM);
                final EditText year = v.findViewById(R.id.inv_YYYY);
                final EditText notes = v.findViewById(R.id.inv_notes);

                // auto shift view focus when entering date
                shiftFocus(day, month, year, notes);

                // add a new investigation action
                builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        // join date into one string
                        final String date = day.getText().toString() + "/" + month.getText().toString()
                                + "/" + year.getText().toString();

                        // check that a title has been given
                        Boolean validTitle = true;
                        if (title.getText().toString().equals("")){
                            validTitle = false;
                        }

                        // check that a valid date was given
                        Boolean validDate = true;
                        if (date.equals("//")) {validDate = false;} // no date given
                        else if (date.length() != 10) {validDate = false;} // incomplete date
                        else {
                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                Date d = sdf.parse(date);
                                if (!date.equals(sdf.format(d))){
                                    validDate = false;
                                }
                            } catch (ParseException e) {e.printStackTrace();}
                        }

                        // format checks passed - add the new investigation to the database
                        if (validTitle && validDate){
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    AppDatabase db = AppDatabase.getAppDatabase(getActivity());
                                    InvestigationsEntity investigation = new InvestigationsEntity(
                                            title.getText().toString(), date, notes.getText().toString());
                                    db.investigationDao().insertAll(investigation);

                                    //TODO: GO TO DETAILS FRAGMENT
                                    // (for now) update the list view
                                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                                    ft.detach(Investigations.this).attach(Investigations.this).commit();
                                }
                            }).start();
                        }

                        // format checks failed - abort and show error message
                        else {
                            if (!validTitle){errorDialog("title");} // bad title
                            else {errorDialog("date");} // bad date
                        }
                    }

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

    /**
     * errorDialog is called when an invalid title or date is part of an investigation
     * being added, it displays an error message about the failure.
     */
    public void errorDialog(String type){

        // set up the dialog
        LayoutInflater inflater = getActivity().getLayoutInflater(); // get inflater
        View v = inflater.inflate(R.layout.format_error, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);

        // specify error type
        final TextView errortype = v.findViewById(R.id.error_type);
        if (type.equals("title")){errortype.setText("YOU MUST PROVIDE A TITLE");}
        if (type.equals("date")){errortype.setText("INVALID DATE");}

        final TextView errormessage = v.findViewById(R.id.error_message);
        errormessage.setText("Your investigation was not added.");

        // user dismiss message
        builder.setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * shiftFocus automatically shifts the fab dialog view focus from day->month and month->year
     * when two digits have been entered for day and month, respectively.
     * @param day is the EditText for the dialog day('DD') field
     * @param month is the EditText for the dialog month('MM') field
     * @param year is the EditText for the dialog year('YYYY') field
     * @param next is the EditText for the dialog field that follows year
     */
    public void shiftFocus(final EditText day, final EditText month, final EditText year, final EditText next){

        day.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (day.getText().toString().length() == 2) {month.requestFocus();}
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        month.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (month.getText().toString().length() == 2) {year.requestFocus();}
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        year.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (year.getText().toString().length() == 4) {next.requestFocus();}
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

    }
}