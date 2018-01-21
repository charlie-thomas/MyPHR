package com.csbgroup.myphr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.csbgroup.myphr.database.AppDatabase;
import com.csbgroup.myphr.database.MedicineEntity;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by JBizzle on 04/12/2017.
 */

public class StatisticsDetails extends Fragment {

    LineGraphSeries<DataPoint> series;
    private FloatingActionButton fab; //the add measurement fab

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

        // TODO: fetch measurement values from database


        Bundle args = getArguments();

        TextView medTitle = rootView.findViewById(R.id.statistics_title);
        medTitle.setText(args.getString("title", "Statistics"));

        GraphView graph = rootView.findViewById(R.id.statistics_graph);
        series = new LineGraphSeries<DataPoint>();
        int date = 0;
        double variable = 50;
        ArrayList<String> list = new ArrayList<String>();
        for(int i =0 ; i<=30; i++){
            series.appendData(new DataPoint(date,variable),true,500);
            list.add("Date:"+Integer.toString(date) + "                         "+ (args.getString("title", "Statistics"))+":"+Double.toString(round(variable,2)));
            date  = date + 1;
            Random r = new Random();
            double randomValue = r.nextDouble() * 2 - 1;
            randomValue /= 10;
            variable += randomValue;
        }
        graph.addSeries(series);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(30);
        graph.getViewport().setXAxisBoundsManual(true);

        ListView listview = (ListView) rootView.findViewById(R.id.statistics_graph_list);
        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, list);
        listview.setAdapter(adapter);

        // fab action for adding medicine
        String type = args.getString("title");
        fab = rootView.findViewById(R.id.stat_fab);
        buildDialog(fab, type);

        return rootView;
    }
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
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

    /**
     * buildDialog builds the pop-up dialog for adding a new measurement instance;
     * in the case of the height velocity fragment it will hide the fab.
     * @param fab the floating action button which pulls up the dialog
     */
    public void buildDialog(FloatingActionButton fab, final String type) {

        // no fab action for height velocity
        if (type.equals("Height Velocity")) {fab.setVisibility(View.GONE); return;}

        // fab action for height and weight (w/ centiles)
        if (type.equals("Height") || type.equals("Weight")){

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // set up the dialog
                    LayoutInflater inflater = getActivity().getLayoutInflater(); // get inflater
                    View v = inflater.inflate(R.layout.add_measurement_centile, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setView(v);

                    // set measurement specific texts
                    final TextView title = v.findViewById(R.id.dialog_title);
                    title.setText("Add a New " + type);
                    final EditText measurement = v.findViewById(R.id.measurement);
                    measurement.setHint(type.toLowerCase());

                    // fetch the input values (measurement already fetched above ^)
                    final EditText date = v.findViewById(R.id.measdate);
                    final EditText centile = v.findViewById(R.id.centile);

                    // add new medicine action
                    builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {

                                    // TODO: add the new measurement to the database

                                }
                            }).start();

                            // TODO: update the list view
                        }
                    });

                    // action for cancelling activity

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });

            return;
        }

        // fab action for all other measurements
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // set up the dialog
                LayoutInflater inflater = getActivity().getLayoutInflater(); // get inflater
                View v = inflater.inflate(R.layout.add_measurement_basic, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setView(v);

                // set measurement specific texts
                if (type.equals("Body Mass Index (BMI)")){
                    final TextView title = v.findViewById((R.id.dialog_title));
                    title.setText("Add a New BMI");
                    final EditText meashint = v.findViewById(R.id.measurement);
                    meashint.setHint("BMI");
                } else {
                    final TextView title = v.findViewById(R.id.dialog_title);
                    title.setText("Add a New " + type);
                    final EditText measurement = v.findViewById(R.id.measurement);
                    measurement.setHint(type.toLowerCase());
                }

                // fetch the input values (measurement fetched above ^)
                final EditText date = v.findViewById(R.id.measdate);

                // add new medicine action
                builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                // TODO: add the new measurement to the database

                            }
                        }).start();

                        // TODO: update the list view
                    }
                });

                // action for cancelling activity

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }

}