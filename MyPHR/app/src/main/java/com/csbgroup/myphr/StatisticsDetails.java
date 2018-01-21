package com.csbgroup.myphr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

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

        View rootView = inflater.inflate(R.layout.fragment_statistics_details, container, false);

        Bundle args = getArguments();

        TextView medTitle = rootView.findViewById(R.id.statistics_title);
        medTitle.setText(args.getString("title", "Measurements"));

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

        ((MainActivity) getActivity()).setToolbar("My Measurements", true);
        setHasOptionsMenu(true);

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

}