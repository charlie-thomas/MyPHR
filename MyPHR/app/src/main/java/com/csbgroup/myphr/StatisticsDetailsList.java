package com.csbgroup.myphr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.csbgroup.myphr.database.AppDatabase;
import com.csbgroup.myphr.database.StatValueEntity;
import com.csbgroup.myphr.database.StatisticsEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static android.view.View.GONE;

/**
 * Created by JBizzle on 04/12/2017.
 */

public class StatisticsDetailsList extends Fragment {

    FloatingActionButton fab; // the add measurement fab
    public static ListView listview;
    public static StatValueAdapter adapter;
    public static boolean isEditMode = false;
    public static View rootView;
    public static String type;

    public StatisticsDetailsList() {
        // Required empty public constructor
    }

    public static StatisticsDetailsList newInstance() {
        StatisticsDetailsList fragment = new StatisticsDetailsList();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // view set up
        rootView = inflater.inflate(R.layout.fragment_statistics_details_list, container, false);
        ((MainActivity) getActivity()).setToolbar("My Measurements", true);
        setHasOptionsMenu(true);
        isEditMode = false;

        Bundle args = getArguments();

        //This formatter is for changing the string entered in form "dd/MM/yyyy" into a Java Date type
        final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date d1 = null;

        //currentstat is a the StatisticsEntity for the current statistics page (e.g weight, height etc)
        final StatisticsEntity currentstat = getStats(args.getString("title", "Statistics"));
        //valueslist is the list of all the entity's in currentstat. Each contains a date, value and centile.
        ArrayList<StatValueEntity> valueslist =  currentstat.getValues();

        //Sorting valueslist so it's ordered in date order, oldest first.
        //Need to do this because the graph must plot from oldest to newest.
        Collections.sort(valueslist, new Comparator<StatValueEntity>(){
            @Override
            public int compare(StatValueEntity t1, StatValueEntity t2) {
                try {
                    return formatter.parse(t1.getDate()).compareTo(formatter.parse(t2.getDate()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });


        /*Reversing the list now so its ordered newest to oldest
          This is so the listview underneath prints from newest to oldest */
        Collections.reverse(valueslist);
        type = args.getString("title");

        listview = (ListView) rootView.findViewById(R.id.statistics_graph_list);
        /*The listview uses a custom adapter which uses an xml to print each list item
        Format located in stat_list_adapter.xml
        Listview is formatted in StatValueAdpater.java */
        adapter = new StatValueAdapter(getActivity(),R.layout.stat_list_adapter, valueslist,type);
        listview.setAdapter(adapter);



        // fab action for adding measurement
        fab = rootView.findViewById(R.id.s_fab);
        buildDialog(fab, type, args);

        return rootView;

    }


    //method to get the StatisticsEntity for a given measurement(e.g weight, height etc)
    public StatisticsEntity getStats(final String unit) {
        // Create a callable object for database transactions
        Callable callable = new Callable() {
            @Override
            public Object call() throws Exception {
                return AppDatabase.getAppDatabase(getActivity()).statisticsDao().getStatistic(unit);
            }
        };

        // Get a Future object of all the statistics titles
        ExecutorService service = Executors.newFixedThreadPool(2);
        Future<StatisticsEntity> result = service.submit(callable);

        // Create a list of the statistics names
        StatisticsEntity statistics = null;
        try {
            statistics = result.get();
        } catch (Exception e) {}

        return statistics;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.edit, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case(R.id.details_edit):
                TabHost tabhost = (TabHost) getActivity().findViewById(R.id.tabHost);
                View thisfab = rootView.findViewById(R.id.s_fab);
                if(tabhost.getCurrentTab() == 0) {
                    if (isEditMode) {
                        isEditMode = false;
                        adapter.notifyDataSetChanged();
                        if(!type.equals("Height Velocity")) {
                            thisfab.setVisibility(View.VISIBLE);
                        }

                    } else {
                        isEditMode = true;
                        adapter.notifyDataSetChanged();
                        thisfab.setVisibility(View.GONE);
                    }
                } else {
                    tabhost.setCurrentTab(0);
                    isEditMode = true;
                    adapter.notifyDataSetChanged();
                    thisfab.setVisibility(View.GONE);
                }
                break;
            case android.R.id.home:
                ((MainActivity) getActivity()).switchFragment(Statistics.newInstance());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void buildDialog(FloatingActionButton fab, final String type, final Bundle args) {

        // no fab for height velocity
        if (type.equals("Height Velocity")) {
            fab.setVisibility(GONE);
            return;
        }

        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // set up the dialog
                View v;
                final EditText cent;
                final EditText diastolic;

                LayoutInflater inflater = getActivity().getLayoutInflater(); // get inflater
                if (type.equals("Height") || type.equals("Weight")) {
                    v = inflater.inflate(R.layout.add_measurement_centile, null);
                    diastolic = null;
                    cent = v.findViewById(R.id.centile);
                } else if (type.equals("Blood Pressure")) {
                    v = inflater.inflate(R.layout.add_measurement_bp, null);
                    diastolic = v.findViewById(R.id.bp_diastolic);
                    cent = null;
                } else {
                    v = inflater.inflate(R.layout.add_measurement_basic, null);
                    diastolic = null;
                    cent = null;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setView(v);

                // set measurement specific texts
                final TextView title = v.findViewById((R.id.dialog_title));
                final EditText measurement = v.findViewById(R.id.measurement);

                if (type.equals("Body Mass Index (BMI)")) {
                    title.setText("Add a New BMI");
                    measurement.setHint("BMI");
                } else if (!type.equals("Blood Pressure")){
                    title.setText("Add a New " + type);
                    measurement.setHint(type);
                }

                // fetch the input values (measurement already fetched above ^)
                final EditText day = v.findViewById(R.id.meas_DD);
                final EditText month = v.findViewById(R.id.meas_MM);
                final EditText year = v.findViewById(R.id.meas_YYYY);

                // auto shift view focus when entering date
                shiftFocus(day, month, year, cent);

                // add a new measurement action
                builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        // join date into one string
                        final String fulldate = day.getText().toString() + "/" + month.getText().toString()
                                + "/" + year.getText().toString();

                        // check that a measurement was given
                        Boolean validMeasurement = true;
                        final String mmnt;
                        if(type.equals("Blood Pressure")){
                            String sys = measurement.getText().toString();
                            String dias = diastolic.getText().toString();
                            if (sys.equals("") || dias.equals("")) {validMeasurement = false;} // incomplete
                            mmnt = sys +"/"+ dias;
                        } else {
                            mmnt = measurement.getText().toString();
                        }

                        if (mmnt.equals("")) {
                            validMeasurement = false;
                        } // no measurement given

                        // check that a valid date was given
                        Boolean validDate = true;
                        if (fulldate.length() != 10) {
                            validDate = false;
                        } // incomplete date
                        else {
                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                Date d = sdf.parse(fulldate);
                                if (!fulldate.equals(sdf.format(d))) {
                                    validDate = false;
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }

                        // check that a valid centile was given for height/weight (providing no centile IS allowed)
                        Boolean validCentile = true;
                        final String centile;
                        if (type.equals("Height") || type.equals("Weight")) {
                            centile = cent.getText().toString();
                            if (!centile.equals("")) {
                                int centileint = Integer.parseInt(centile); // convert to an int for checks
                                if ((centileint < 0 || centileint > 100)) {
                                    validCentile = false;
                                }
                            }
                        } else {
                            centile = null;
                        }

                        //format checks passed - add the new measurement to the database
                        if (validMeasurement && validDate && validCentile) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    AppDatabase db = AppDatabase.getAppDatabase(getActivity());
                                    final StatisticsEntity thisstat = getStats(type);
                                    thisstat.addValue(mmnt, fulldate, centile);
                                    db.statisticsDao().update(thisstat);

                                    Fragment details = StatisticsSection.newInstance();

                                    Bundle bundle = new Bundle();
                                    bundle.putString("title", args.getString("title", "Measurements"));
                                    details.setArguments(bundle);

                                    ((MainActivity) getActivity()).switchFragment(details);
                                }
                            }).start();
                        }

                        // format checks failed - abort and show error message
                        else {
                            if (!validMeasurement) {
                                errorDialog("measurement");
                            } // no measurement
                            else if (!validDate) {
                                errorDialog("date");
                            } // bad date
                            else {
                                errorDialog("centile");
                            } // bad centile
                        }
                    }
                });

                // action for cancelling add
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    /**
     * errorDialog is called when an invalid measurement, date or centile is part of a measurement
     * being added, it displays an error message about the failure.
     * @param type is the type of error reported
     */
    public void errorDialog(String type){

        // set up the dialog
        LayoutInflater inflater = getActivity().getLayoutInflater(); // get inflater
        View v = inflater.inflate(R.layout.format_error, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);

        // specify error type
        final TextView errortype = v.findViewById(R.id.error_type);
        if (type.equals("measurement")){errortype.setText("YOU MUST PROVIDE A MEASUREMENT");}
        if (type.equals("centile")){errortype.setText("INVALID CENTILE");}
        if (type.equals("date")){errortype.setText("INVALID DATE");}

        final TextView errormessage = v.findViewById(R.id.error_message);
        errormessage.setText("Your measurement was not added.");

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

        if (next != null) {
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

}