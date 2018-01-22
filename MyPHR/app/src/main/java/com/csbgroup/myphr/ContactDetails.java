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

import com.csbgroup.myphr.database.AppDatabase;
import com.csbgroup.myphr.database.ContactsEntity;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

        // fill in the values

        Bundle args = getArguments();
        ContactsEntity contact = getContact(args.getString("name"));

        TextView contactTitle = rootView.findViewById(R.id.contact_title);
        contactTitle.setText(contact.getName());

        TextView email = rootView.findViewById(R.id.email);
        email.setText(contact.getEmail());

        TextView phone = rootView.findViewById(R.id.phone);
        phone.setText(contact.getPhone());

        TextView notes = rootView.findViewById(R.id.notes);
        notes.setText(contact.getNotes());

        // back button
        ((MainActivity) getActivity()).setToolbar("My Contacts", true);
        setHasOptionsMenu(true);

        return rootView;
    }

    /**
     * Fetches a single contact entity from the database, found by name
     * @param name is the name of the contact to be retrieved
     * @return the contact entity
     */
    private ContactsEntity getContact(final String name) {

        // Create a callable object for database transactions
        Callable callable = new Callable() {
            @Override
            public Object call() throws Exception {
                return AppDatabase.getAppDatabase(getActivity()).contactsDao().getContact(name);
            }
        };

        ExecutorService service = Executors.newFixedThreadPool(2);
        Future<ContactsEntity> result = service.submit(callable);

        ContactsEntity contact = null;
        try {
            contact = result.get();
        } catch (Exception e) {}

        return contact;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.edit, menu);
    }

    /**
     * Provides navigation for menu items; currenty only needed for navigation back to the
     * main contacts fragment.
     * @param item the clicked menu item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // back button
                ((MainActivity) getActivity()).switchFragment(Contacts.newInstance());
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}