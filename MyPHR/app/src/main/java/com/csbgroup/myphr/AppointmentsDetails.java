package com.csbgroup.myphr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AppointmentsDetails extends Fragment {

    public AppointmentsDetails() {
        // Required empty public constructor
    }

    public static AppointmentsDetails newInstance() {
        AppointmentsDetails fragment = new AppointmentsDetails();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_appointments_details, container, false);

        Bundle args = getArguments();

        TextView apptTitle = rootView.findViewById(R.id.appointments_title);
        apptTitle.setText(args.getString("title", "Appointment"));

        TextView apptInfo = rootView.findViewById(R.id.appointments_info);
        apptInfo.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam " +
                "facilisis magna vel volutpat blandit. Etiam id ex urna. Nunc luctus justo " +
                "eget lorem consequat, quis efficitur ipsum aliquet. Integer tristique tortor " +
                "vitae augue finibus, non vulputate tortor vulputate. Interdum et malesuada " +
                "fames ac ante ipsum primis in faucibus.");

        TextView notes = rootView.findViewById(R.id.notes);
        notes.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam " +
                "facilisis magna vel volutpat blandit. Etiam id ex urna. Nunc luctus justo " +
                "eget lorem consequat, quis efficitur ipsum aliquet. Integer tristique tortor " +
                "vitae augue finibus, non vulputate tortor vulputate. Interdum et malesuada " +
                "fames ac ante ipsum primis in faucibus.");

        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.edit, menu);
    }

}