package com.csbgroup.myphr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.TextView;

import com.csbgroup.myphr.database.AppDatabase;
import com.csbgroup.myphr.database.StatValueEntity;
import com.csbgroup.myphr.database.StatisticsEntity;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
/**
 * Created by JBizzle on 04/12/2017.
 */

public class StatisticsDetails extends Fragment {

    LineGraphSeries<DataPoint> series;
    FloatingActionButton fab; // the add measurement fab

    public StatisticsDetails() {
        // Required empty public constructor
    }

    public static StatisticsDetails newInstance() {
        StatisticsDetails fragment = new StatisticsDetails();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // view set up
        View rootView = inflater.inflate(R.layout.fragment_statistics_details, container, false);
        ((MainActivity) getActivity()).setToolbar("My Statistics", true);
        setHasOptionsMenu(true);

        Bundle args = getArguments();

        TextView medTitle = rootView.findViewById(R.id.statistics_title);
        medTitle.setText(args.getString("title", "Measurements"));

        // Setting up the variable for the graph/list
        GraphView graph = rootView.findViewById(R.id.statistics_graph);
        series = new LineGraphSeries<DataPoint>();

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

        //Iterating through the valueslist we format each string date intot a java Date and add it as a datapoint
        for(int i=0;i<valueslist.size();i++){
            StatValueEntity sve = valueslist.get(i);
            try {
                d1 = formatter.parse(sve.getDate());
                DataPoint dp = new DataPoint(d1,Double.parseDouble(sve.getValue())); //added as a datapoint here
                series.appendData(dp,true,valueslist.size()); //adding the datapoint to the graph series here
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        /*Reversing the list now so its ordered newest to oldest
          This is so the listview underneath prints from newest to oldest */
        Collections.reverse(valueslist);

        ListView listview = (ListView) rootView.findViewById(R.id.statistics_graph_list);
        /*The listview uses a custom adapter which uses an xml to print each list item
        Format located in stat_list_adapter.xml
        Listview is formatted in StatValueAdpater.java */
        StatValueAdapter adapter = new StatValueAdapter(getActivity(),R.layout.stat_list_adapter, valueslist);
        listview.setAdapter(adapter);


        //All of these "graph." make adjustments to the graph so it displays correctly
        graph.addSeries(series); //adds the datapoint series to the graph
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
        graph.getGridLabelRenderer().setNumHorizontalLabels(4);
        graph.getGridLabelRenderer().setTextSize(25);
        graph.getViewport().setScrollable(true);
        graph.getGridLabelRenderer().setHumanRounding(false);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getGridLabelRenderer().setVerticalAxisTitle(args.getString("title","Statistics"));
        graph.getGridLabelRenderer().setVerticalAxisTitleTextSize(35);
        graph.getGridLabelRenderer().setPadding(58);
        graph.getGridLabelRenderer().setLabelVerticalWidth(75);

        //this if statement allows for the graph to keep four values at a time and begin scrolling after 4 have been added.
        if(currentstat.getValues().size() > 4){
            try {
                Date mindate = formatter.parse(currentstat.getValues().get(currentstat.getValues().size()-4).getDate());
                graph.getViewport().setMinX(mindate.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        // fab action for adding measurement
        String type = args.getString("title");
        fab = rootView.findViewById(R.id.s_fab);
        buildDialog(fab, type);

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

    /* Navigation from details fragment back to Statistics */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ((MainActivity) getActivity()).switchFragment(Statistics.newInstance());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void buildDialog(FloatingActionButton fab, final String type){

        // no fab for height velocity
        if (type.equals("Height Velocity")){fab.setVisibility(View.GONE); return;}

        // fab action for height and weight (w/centiles)
        if (type.equals("Height") || type.equals("Weight")){

            fab.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view){

                    // set up the dialog
                    LayoutInflater inflater = getActivity().getLayoutInflater(); // get inflater
                    View v = inflater.inflate(R.layout.add_measurement_centile, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setView(v);

                    // set measurement specific texts
                    final TextView title = v.findViewById(R.id.dialog_title);
                    title.setText("Add a New " + type);
                    final EditText measurement = v.findViewById(R.id.measurement);
                    measurement.setHint(type);

                    // fetch the input values (measurement already fetched above ^)
                    final EditText day = v.findViewById(R.id.meas_DD);
                    final EditText month = v.findViewById(R.id.meas_MM);
                    final EditText year = v.findViewById(R.id.meas_YYYY);
                    final EditText cent = v.findViewById(R.id.centile);

                    // auto shift view focus when entering date
                    shiftFocus(day, month, year, measurement);

                    // add a new measurement action
                    builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            new Thread(new Runnable(){

                                @Override
                                public void run() {

                                    AppDatabase db = AppDatabase.getAppDatabase(getActivity());

                                    // join date into one string
                                    String fulldate = day.getText().toString() + "/" + month.getText().toString()
                                            + "/" + year.getText().toString();

                                    String centile = cent.getText().toString();

                                    // valid date string checking
                                    Date date = null;
                                    try {
                                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                        date = sdf.parse(fulldate);

                                        if (!fulldate.equals(sdf.format(date))) {
                                            date = null;
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    if (date != null) {
                                        final StatisticsEntity thisstat = getStats(type);
                                        thisstat.addValue(measurement.getText().toString(), fulldate, centile);
                                        db.statisticsDao().update(thisstat);
                                    }
                                }
                            }).start();

                            // update the list view
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            ft.detach(StatisticsDetails.this).attach(StatisticsDetails.this).commit();
                        }
                    });

                    // dismiss dialog, cancel add
                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
            return;
        }

        // fab action for all other measurement types
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // set up the dialog
                LayoutInflater inflater = getActivity().getLayoutInflater(); // get inflater
                View v = inflater.inflate(R.layout.add_measurement_basic, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setView(v);

                // set measurement specific texts
                final TextView title = v.findViewById((R.id.dialog_title));
                final EditText measurement = v.findViewById(R.id.measurement);
                if (type.equals("Body Mass Index (BMI)")) {
                    title.setText("Add a New BMI");
                    measurement.setHint("BMI");
                } else {
                    title.setText("Add a New " + type);
                    measurement.setHint(type);
                }

                // fetch the input values (measurement fetched above ^)
                final EditText day = v.findViewById(R.id.meas_DD);
                final EditText month = v.findViewById(R.id.meas_MM);
                final EditText year = v.findViewById(R.id.meas_YYYY);

                // auto shift view focus when entering date
                shiftFocus(day, month, year, measurement);

                // add new measurement action
                builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                AppDatabase db = AppDatabase.getAppDatabase(getActivity());

                                // join date into one string
                                String fulldate = day.getText().toString() + "/" + month.getText().toString()
                                        + "/" + year.getText().toString();

                                String centile = null;

                                // valid date string checking
                                Date date = null;
                                try {
                                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                    date = sdf.parse(fulldate);

                                    if (!fulldate.equals(sdf.format(date))) {
                                        date = null;
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                // add the new measurement to the database
                                if (date != null) {
                                    final StatisticsEntity thisstat = getStats(type);
                                    thisstat.addValue(measurement.getText().toString(), fulldate, centile);
                                    db.statisticsDao().update(thisstat);
                                }
                            }
                        }).start();

                        // update the list view
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.detach(StatisticsDetails.this).attach(StatisticsDetails.this).commit();
                    }
                });

                // dismiss dialog, cancel add
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