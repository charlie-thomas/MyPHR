package com.csbgroup.myphr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ContactDetails extends Fragment {

    public ContactDetails() {
        // Required empty public constructor
    }

    public static ContactDetails newInstance() {
        ContactDetails fragment = new ContactDetails();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_contact_details, container, false);

        Bundle args = getArguments();

        TextView contactTitle = rootView.findViewById(R.id.contact_title);
        contactTitle.setText(args.getString("title", "Dr. A"));

        TextView email = rootView.findViewById(R.id.email);
        email.setText("Lorem@ipsum.dolor.sit");

        TextView phone = rootView.findViewById(R.id.phone);
        phone.setText("0123456789");

        TextView notes = rootView.findViewById(R.id.notes);
        notes.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam " +
                "facilisis magna vel volutpat blandit. Etiam id ex urna. Nunc luctus justo " +
                "eget lorem consequat, quis efficitur ipsum aliquet. Integer tristique tortor " +
                "vitae augue finibus, non vulputate tortor vulputate. Interdum et malesuada " +
                "fames ac ante ipsum primis in faucibus.");

        ((MainActivity) getActivity()).setToolbar("My Contacts", true);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ((MainActivity) getActivity()).switchFragment(Contacts.newInstance());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}