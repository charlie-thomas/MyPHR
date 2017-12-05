package com.csbgroup.myphr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ContactDetails extends Fragment {

    public ContactDetails() {
        // Required empty public constructor
    }

    public static MedicineDetails newInstance() {
        MedicineDetails fragment = new MedicineDetails();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_medicine_details, container, false);

        Bundle args = getArguments();

        TextView medTitle = rootView.findViewById(R.id.medicine_title);
        medTitle.setText(args.getString("title", "Medicine A"));

        TextView medInfo = rootView.findViewById(R.id.medicine_info);
        medInfo.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam " +
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