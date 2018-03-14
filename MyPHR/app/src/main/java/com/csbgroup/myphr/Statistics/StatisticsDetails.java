package com.csbgroup.myphr.Statistics;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.csbgroup.myphr.MainActivity;
import com.csbgroup.myphr.R;
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

public class StatisticsDetails extends Fragment {

    LineGraphSeries<DataPoint> series;
    ArrayList<StatValueEntity> valuesList;
    final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    String title;
    GraphView graph;

    public StatisticsDetails() {} // Required empty public constructor

    public static StatisticsDetails newInstance() {
        StatisticsDetails fragment = new StatisticsDetails();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // view set up
        View rootView = inflater.inflate(R.layout.fragment_statistics_details, container, false);
        ((MainActivity) getActivity()).setToolbar("", true);
        setHasOptionsMenu(true);

        Bundle args = getArguments();
        title = args.getString("title","Measurements");

        //currentStat is a the StatisticsEntity for the current statistics page (e.g weight, height etc)
        final StatisticsEntity currentStat = getStats(args.getString("title", "Statistics"));

        //valuesList is the list of all the entity's in currentStat. Each contains a date, value and centile.
        valuesList = currentStat.getValues();

        // order valuesList by oldest date first so that graph plots old -> new
        Collections.sort(valuesList, new Comparator<StatValueEntity>() {
            @Override
            public int compare(StatValueEntity t1, StatValueEntity t2) {
                try {
                    return formatter.parse(t1.getDate()).compareTo(formatter.parse(t2.getDate()));
                }
                catch (ParseException e) {e.printStackTrace();}
                return 0;
            }
        });

        // Setting up the variable for the graph/list
        graph = rootView.findViewById(R.id.statistics_graph);

        // Loading data onto the graph
        ArrayList<LineGraphSeries<DataPoint>> seriesList = createDataPoints();
        graph = createGraph(seriesList.get(0));
        if(title.equals("Blood Pressure")){
            graph.addSeries(seriesList.get(1));
            graph.getLegendRenderer().setVisible(true);
            graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        }

        // Show "No measurements" message if measurements list empty
        LinearLayout no_stats = rootView.findViewById(R.id.no_stats);
        no_stats.setVisibility(View.INVISIBLE);
        if (valuesList.size() == 0) {
            graph.setVisibility(View.GONE);
            no_stats.setVisibility(View.VISIBLE);
        }

        return rootView;
    }

    /**
     * getStats fetches the statisticsEntity for a given measurement type from the database.
     * @param unit is the measurement type (height/weight/BMI...)
     * @return the requested statisticsEntity
     */
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
        }
        catch (Exception e) {}
        return statistics;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {}

    /**
     * onOptionsItemSelected provides navigation/actions for menu items.
     * @param item is the clicked menu item
     * @return super.onOptionsItemSelected(item)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    /**
     * createDataPoints plots the data points onto the measurements graph.
     * @return the array list of linegraphseries datapoints
     */
    public ArrayList<LineGraphSeries<DataPoint>> createDataPoints() {

        ArrayList<LineGraphSeries<DataPoint>> seriesList = new ArrayList();
        series = new LineGraphSeries();
        LineGraphSeries<DataPoint> series2 = new LineGraphSeries();
        Date d1;

        //Iterating through the valuesList, we format each string date into a java Date and add it as a datapoint
        for (int i = 0; i < valuesList.size(); i++) {
            StatValueEntity sve = valuesList.get(i);
            try {
                d1 = formatter.parse(sve.getDate());

                //If blood pressure, we add the systolic value to one line series, and the dialostic to another.
                //This is because we plot two lines for blood pressure
                if (title.equals("Blood Pressure")) {
                    DataPoint dp = new DataPoint(d1, Double.parseDouble(sve.getValue().substring(0, sve.getValue().indexOf("/")))); //adding the systolic value as a datapoint here
                    DataPoint dp2 = new DataPoint(d1, Double.parseDouble(sve.getValue().substring(sve.getValue().indexOf("/") + 1, sve.getValue().length()))); //adding the dialostic value as a datapoint here
                    series.appendData(dp, true, valuesList.size()); //adding the datapoint to the graph series here
                    series2.appendData(dp2, true, valuesList.size()); //adding the datapoint to the graph series here

                } else {
                    DataPoint dp = new DataPoint(d1, Double.parseDouble(sve.getValue())); //added value as a datapoint here
                    series.appendData(dp, true, valuesList.size()); //adding the datapoint to the graph series here
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        //Making main graph color red/accent colour
        //Making secondary graph color (used for diastolic line) blue
        series.setColor(Color.parseColor("#E91E63"));
        series2.setColor(Color.BLUE);
        series.setThickness(7); //for a more visible line
        series2.setThickness(7);

        //adding blood pressure titles for the legend
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

    /**
     * createGraph displays the graph of measurements.
     * @param series is the datapoint series for the graph
     * @return the graph
     */
    private GraphView createGraph(LineGraphSeries<DataPoint> series) {

        graph.addSeries(series); //adds the datapoint series to the graph

        // graph display adjustments
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

    /**
     * checkGraphMin checks the number of data points on the graph and allows scrolling of the graph
     * view after 4.
     */
    private void checkGraphMin(){

        //this if statement allows for the graph to keep four values at a time and begin scrolling after 4 have been added.
        if (valuesList.size() > 4) {
            try {
                Date minDate = formatter.parse(valuesList.get(valuesList.size() - 4).getDate());
                graph.getViewport().setMinX(minDate.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else try {
            if(valuesList.size()>0) graph.getViewport().setMinX(formatter.parse(valuesList.get(0).getDate()).getTime());
        }
        catch (ParseException e) {e.printStackTrace();}
    }
}