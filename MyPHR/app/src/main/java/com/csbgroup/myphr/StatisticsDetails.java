package com.csbgroup.myphr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.csbgroup.myphr.database.AppDatabase;
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
import java.util.Calendar;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
/**
 * Created by JBizzle on 04/12/2017.
 */

public class StatisticsDetails extends Fragment {

    LineGraphSeries<DataPoint> series;
    FloatingActionButton but;

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
        final ArrayList<String> list = new ArrayList<String>();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date d1 = null;
        final StatisticsEntity currentstat = getStats(args.getString("title", "Statistics"));
        ArrayList<DataPoint> dpa = new ArrayList<DataPoint>();
        for(int i=0;i<currentstat.getValues().size();i++){
                String variable = currentstat.getValues().get(i).getValue();
                String stringdate = currentstat.getValues().get(i).getDate();

            try {
                d1 = formatter.parse(stringdate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            dpa.add(new DataPoint(d1,Double.parseDouble(variable)));
            list.add("Date:"+stringdate + "                   "+ (args.getString("title", "Statistics"))+":"+Double.parseDouble(variable));
        }
        Collections.sort(dpa, new Comparator<DataPoint>() {
            @Override
            public int compare(DataPoint o1, DataPoint o2) {
                return Double.compare(o1.getX(),o2.getX());
            }
        });
        DataPoint[] DataPointArray = dpa.toArray(new DataPoint[dpa.size()]);
        series.resetData(DataPointArray);
        Collections.reverse(list);

        graph.addSeries(series);
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
        graph.getGridLabelRenderer().setNumHorizontalLabels(4);
        graph.getGridLabelRenderer().setTextSize(25);
        graph.getViewport().setScrollable(true);
        graph.getGridLabelRenderer().setHumanRounding(false);
        graph.getViewport().setXAxisBoundsManual(true);
        if(currentstat.getValues().size() > 4){
            try {
                Date mindate = formatter.parse(currentstat.getValues().get(currentstat.getValues().size()-4).getDate());
                graph.getViewport().setMinX(mindate.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        graph.getGridLabelRenderer().setVerticalAxisTitle(args.getString("title","Statistics"));
        graph.getGridLabelRenderer().setVerticalAxisTitleTextSize(35);
        graph.getGridLabelRenderer().setPadding(58);
        graph.getGridLabelRenderer().setLabelVerticalWidth(75);
        ListView listview = (ListView) rootView.findViewById(R.id.statistics_graph_list);
        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, list);
        listview.setAdapter(adapter);

        ((MainActivity) getActivity()).setToolbar("My Statistics", true);
        setHasOptionsMenu(true);

        but = (FloatingActionButton) rootView.findViewById(R.id.s_fab);
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
                View mView = getLayoutInflater().inflate(R.layout.stat_dialog,null);
                final EditText value = (EditText) mView.findViewById(R.id.etValue);
                final EditText datedd = (EditText) mView.findViewById(R.id.etDatedd);
                final EditText datemm = (EditText) mView.findViewById(R.id.etDatemm);
                final EditText dateyyyy = (EditText) mView.findViewById(R.id.etDateyyyy);

                ab.setView(mView);

                ab.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                AppDatabase db = AppDatabase.getAppDatabase(getActivity());
                                //StatisticsEntity st = new StatisticsEntity(et.getText().toString());


                                String day = datedd.getText().toString();
                                String month = datemm.getText().toString();
                                String year = dateyyyy.getText().toString();
                                String fulldate = day+"/"+month+"/"+year;

                                Date date = null;
                                try {
                                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                    date = sdf.parse(fulldate);
                                    if (!fulldate.equals(sdf.format(date))) {
                                        date = null;
                                    }
                                } catch (ParseException ex) {
                                    ex.printStackTrace();
                                }
                                if (date != null) {
                                    currentstat.addValue(value.getText().toString(),fulldate,null);
                                    db.statisticsDao().update(currentstat);
                                }
                            }
                        }).start();
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.detach(StatisticsDetails.this).attach(StatisticsDetails.this).commit();
                    }
                });
                ab.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });

                final AlertDialog dialog = ab.create();
                dialog.show();
            }
        });

        return rootView;
    }


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
    static <T> T[] append(T[] arr, T element) {
        final int N = arr.length;
        arr = Arrays.copyOf(arr, N + 1);
        arr[N] = element;
        return arr;
    }

}