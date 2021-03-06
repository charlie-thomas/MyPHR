package com.csbgroup.myphr.Statistics;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

import com.csbgroup.myphr.MainActivity;
import com.csbgroup.myphr.R;

public class StatisticsSection extends Fragment {

    public StatisticsSection() {} // Required empty public constructor

    public static StatisticsSection newInstance() {
        StatisticsSection fragment = new StatisticsSection();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // set up the view
        final View rootView =  inflater.inflate(R.layout.fragment_statistics_section, container, false);
        ((MainActivity) getActivity()).setToolbar("Graph", false);
        setHasOptionsMenu(true);

        // Set up TabHost
        final TabHost mTabHost = rootView.findViewById(R.id.tabHost);
        mTabHost.setup();

        // Add first tab for the calendar
        Bundle args = getArguments();
        TabHost.TabSpec mSpec = mTabHost.newTabSpec(args.getString("title", "Measurements"));
        mSpec.setContent(R.id.list_tab);
        mSpec.setIndicator(args.getString("title", "Measurements"));
        mTabHost.addTab(mSpec);

        // Add second tab for the investigations
        mSpec = mTabHost.newTabSpec("Graph");
        mSpec.setContent(R.id.graph_tab);
        mSpec.setIndicator("Graph");
        mTabHost.addTab(mSpec);

        final Fragment details = StatisticsDetails.newInstance();
        final Fragment detailsList = StatisticsDetailsList.newInstance();

        // Create a bundle to pass the statistics name to the graph/list fragment
        final Bundle bundle = new Bundle();
        bundle.putString("title", args.getString("title", "Measurements"));

        details.setArguments(bundle);
        detailsList.setArguments(bundle);

        // Add content to tabs
        final FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.graph_tab, details);
        transaction.replace(R.id.list_tab, detailsList);
        transaction.commit();

        //if tab is changed then change the edit mode to false so the delete btn's don't remain
        //if tab is changed to the graph tab then reload the graph in case changes have been made from the list page
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {
                StatisticsDetailsList.isEditMode = false;
                StatisticsDetailsList.adapter.notifyDataSetChanged();

                // show the add fab
                if(!StatisticsDetailsList.type.equals("Height Velocity")) {
                    StatisticsDetailsList.rootView.findViewById(R.id.s_fab).setVisibility(View.VISIBLE);
                }
                // switch to graph tab
                if(s.equals("Graph")) {
                    Fragment nextFrag = StatisticsDetails.newInstance();
                    nextFrag.setArguments(bundle);
                    FragmentTransaction transaction2 = getFragmentManager().beginTransaction();
                    transaction2.addToBackStack(null);
                    transaction2.replace(R.id.graph_tab, nextFrag);
                    transaction2.commit();
                }
            }
        });
        return rootView;
    }
}
