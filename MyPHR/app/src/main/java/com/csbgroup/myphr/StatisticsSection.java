package com.csbgroup.myphr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

public class StatisticsSection extends Fragment {

    public StatisticsSection() {
        // Required empty public constructor
    }

    public static StatisticsSection newInstance() {
        StatisticsSection fragment = new StatisticsSection();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_statistics_section, container, false);

        ((MainActivity) getActivity()).setToolbar("Graph", false);
        setHasOptionsMenu(true);

        // Set up TabHost
        TabHost mTabHost = rootView.findViewById(R.id.tabHost);
        mTabHost.setup();

        // Add first tab for the calendar
        TabHost.TabSpec mSpec = mTabHost.newTabSpec("List");
        mSpec.setContent(R.id.list_tab);
        mSpec.setIndicator("List");
        mTabHost.addTab(mSpec);

        // Add second tab for the investigations
        mSpec = mTabHost.newTabSpec("Graph");
        mSpec.setContent(R.id.graph_tab);
        mSpec.setIndicator("Graph");
        mTabHost.addTab(mSpec);

        Fragment details = StatisticsDetails.newInstance();
        Fragment detailsList = StatisticsDetailsList.newInstance();
        Bundle args = getArguments();
        // Create a bundle to pass the statistics name to the graph/list fragment
        Bundle bundle = new Bundle();
        bundle.putString("title", args.getString("title", "Measurements"));

        details.setArguments(bundle);
        detailsList.setArguments(bundle);

        // Add content to tabs
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.graph_tab, details);
        transaction.replace(R.id.list_tab, detailsList);
        transaction.commit();

        return rootView;
    }

}
