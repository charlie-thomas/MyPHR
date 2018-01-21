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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.csbgroup.myphr.database.AppDatabase;
import com.csbgroup.myphr.database.StatisticsDao;
import com.csbgroup.myphr.database.StatisticsEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Statistics extends Fragment {

    FloatingActionButton but;

    public Statistics() {
        // Required empty public constructor
    }

    public static Statistics newInstance() {
        Statistics fragment = new Statistics();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_statistics, container, false);

        ((MainActivity) getActivity()).setToolbar("My Statistics", false);
        setHasOptionsMenu(true);


        List<StatisticsEntity> stats = getStats();

        List<String> statistics = new ArrayList<String>();
        for (StatisticsEntity st : stats) {
            statistics.add(st.getUnit());
        }

        ArrayAdapter<String> statisticsAdapter = new ArrayAdapter<>(
                getActivity(),
                R.layout.simple_list_item,
                statistics);

        ListView listView = rootView.findViewById(R.id.statistics_list);
        listView.setAdapter(statisticsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment details = StatisticsDetails.newInstance();

                // Create a bundle to pass the medicine name to the details fragment
                Bundle bundle = new Bundle();
                bundle.putString("title", parent.getAdapter().getItem(position).toString());
                details.setArguments(bundle);

                ((MainActivity) getActivity()).switchFragment(details);

            }
        });


        return rootView;
    }

    private List<StatisticsEntity> getStats() {
        // Create a callable object for database transactions
        Callable callable = new Callable() {
            @Override
            public Object call() throws Exception {
                return AppDatabase.getAppDatabase(getActivity()).statisticsDao().getAll();
            }
        };

        // Get a Future object of all the medicine titles
        ExecutorService service = Executors.newFixedThreadPool(2);
        Future<List<StatisticsEntity>> result = service.submit(callable);

        // Create a list of the appointment names
        List<StatisticsEntity> statistics = null;
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
        inflater.inflate(R.menu.settings, menu);
    }

    /* Navigation from Statistics to settings fragment */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            ((MainActivity) getActivity()).switchFragment(StatisticsSettings.newInstance());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}