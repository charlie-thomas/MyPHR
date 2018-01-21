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
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Calendar;

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
        medTitle.setText(args.getString("title", "Statistics"));

        GraphView graph = rootView.findViewById(R.id.statistics_graph);
        series = new LineGraphSeries<DataPoint>();
        int date = 1;
        double variable = 50;
        ArrayList<String> list = new ArrayList<String>();
        Calendar calendar = Calendar.getInstance();
        Date d = calendar.getTime();
        Date d1 = calendar.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        for(int i =0 ; i<=30; i++){
            series.appendData(new DataPoint(d1,variable),true,500);
            calendar.add(Calendar.DATE,1);
            d1 = calendar.getTime();
            String stringdate = formatter.format(d1);
            list.add("Date:"+stringdate + "                         "+ (args.getString("title", "Statistics"))+":"+Double.toString(round(variable,2)));
            Random r = new Random();
            double randomValue = r.nextDouble() * 2 - 1;
            randomValue /= 10;
            variable += randomValue;
        }
        graph.addSeries(series);
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
        graph.getGridLabelRenderer().setNumHorizontalLabels(4);
        graph.getGridLabelRenderer().setTextSize(25);
        graph.getViewport().setScrollable(true);
        graph.getViewport().setScalable(true);
        graph.getGridLabelRenderer().setHumanRounding(false);
        graph.getViewport().setXAxisBoundsManual(true);
        calendar.add(Calendar.DATE,-4);
        d1 = calendar.getTime();
        graph.getViewport().setMinX(d1.getTime());
        graph.getGridLabelRenderer().setVerticalAxisTitle(args.getString("title","Statistics"));
        graph.getGridLabelRenderer().setVerticalAxisTitleTextSize(35);
        graph.getGridLabelRenderer().setPadding(58);
        graph.getGridLabelRenderer().setLabelVerticalWidth(75);
        ListView listview = (ListView) rootView.findViewById(R.id.statistics_graph_list);
        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, list);
        listview.setAdapter(adapter);

        ((MainActivity) getActivity()).setToolbar("My Statistics", true);
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