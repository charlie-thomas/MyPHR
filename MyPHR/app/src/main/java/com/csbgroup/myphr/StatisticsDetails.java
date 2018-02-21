package com.csbgroup.myphr;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.csbgroup.myphr.database.AppDatabase;
import com.csbgroup.myphr.database.StatValueEntity;
import com.csbgroup.myphr.database.StatisticsEntity;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

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
/**
 * Created by JBizzle on 04/12/2017.
 */

public class StatisticsDetails extends Fragment {

    LineGraphSeries<DataPoint> series;
    ArrayList<StatValueEntity> valueslist;
    final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    String title;
    GraphView graph;

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
        ((MainActivity) getActivity()).setToolbar("My Measurements", true);
        setHasOptionsMenu(true);

        Bundle args = getArguments();

        title = args.getString("title","Measurements");
        TextView medTitle = rootView.findViewById(R.id.statistics_title);
        medTitle.setText(title);

        //currentstat is a the StatisticsEntity for the current statistics page (e.g weight, height etc)
        final StatisticsEntity currentstat = getStats(args.getString("title", "Statistics"));

        //valueslist is the list of all the entity's in currentstat. Each contains a date, value and centile.
        valueslist = currentstat.getValues();

        //Sorting valueslist so it's ordered in date order, oldest first.
        //Need to do this because the graph must plot from oldest to newest.
        Collections.sort(valueslist, new Comparator<StatValueEntity>() {
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

        // Setting up the variable for the graph/list
        graph = rootView.findViewById(R.id.statistics_graph);

        // Loading data onto the graph
        ArrayList<LineGraphSeries<DataPoint>> serieslist = createDataPoints();
        graph = createGraph(serieslist.get(0));
        if(title.equals("Blood Pressure")){
            graph.addSeries(serieslist.get(1));
            graph.getLegendRenderer().setVisible(true);
            graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        }

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
        } catch (Exception e) {
        }

        return statistics;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    }

    /* Navigation from details fragment back to Statistics */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


    public ArrayList<LineGraphSeries<DataPoint>> createDataPoints() {
        ArrayList<LineGraphSeries<DataPoint>> seriesList = new ArrayList<LineGraphSeries<DataPoint>>();
        series = new LineGraphSeries<DataPoint>();
        LineGraphSeries<DataPoint> series2 = new LineGraphSeries<DataPoint>();
        Date d1;
        //Iterating through the valueslist we format each string date intot a java Date and add it as a datapoint
        for (int i = 0; i < valueslist.size(); i++) {
            StatValueEntity sve = valueslist.get(i);
            try {
                d1 = formatter.parse(sve.getDate());

                if(title.equals("Blood Pressure")){
                    DataPoint dp = new DataPoint(d1, Double.parseDouble(sve.getValue().substring(0,sve.getValue().indexOf("/"))));
                    DataPoint dp2 = new DataPoint(d1, Double.parseDouble(sve.getValue().substring(sve.getValue().indexOf("/")+1,sve.getValue().length())));
                    series.appendData(dp, true, valueslist.size()); //adding the datapoint to the graph series here
                    series2.appendData(dp2, true, valueslist.size()); //adding the datapoint to the graph series here

                } else {
                    DataPoint dp = new DataPoint(d1, Double.parseDouble(sve.getValue())); //added as a datapoint here]
                    series.appendData(dp, true, valueslist.size()); //adding the datapoint to the graph series here
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        series2.setColor(Color.RED);
        if(title.equals("Blood Pressure")){
            series.setTitle("Systolic");
            series2.setTitle("Diastolic");
            seriesList.add(series);
            seriesList.add(series2);
        } else {
            seriesList.add(series);
        }
        return seriesList;
    }

    private GraphView createGraph(LineGraphSeries<DataPoint> series) {

        //All of these "graph." make adjustments to the graph so it displays correctly
        graph.addSeries(series); //adds the datapoint series to the graph
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
        graph.getGridLabelRenderer().setNumHorizontalLabels(4);
        graph.getGridLabelRenderer().setTextSize(25);
        graph.getViewport().setScrollable(true);
        graph.getGridLabelRenderer().setHumanRounding(false);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getGridLabelRenderer().setVerticalAxisTitle(title);
        graph.getGridLabelRenderer().setVerticalAxisTitleTextSize(35);
        graph.getGridLabelRenderer().setPadding(58);
        graph.getGridLabelRenderer().setLabelVerticalWidth(75);
        graph.getViewport().scrollToEnd();
        checkGraphMin();

        return graph;

    }

    private void checkGraphMin(){

        //this if statement allows for the graph to keep four values at a time and begin scrolling after 4 have been added.
        if (valueslist.size() > 4) {
            try {
                Date minDate = formatter.parse(valueslist.get(valueslist.size() - 4).getDate());
                graph.getViewport().setMinX(minDate.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else try {
            if(valueslist.size()>0) graph.getViewport().setMinX(formatter.parse(valueslist.get(0).getDate()).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}