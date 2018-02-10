package com.csbgroup.myphr;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.text.method.KeyListener;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.csbgroup.myphr.database.AppDatabase;
import com.csbgroup.myphr.database.ContactsEntity;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ContactDetails extends Fragment {

    private Menu editMenu;
    private String mode = "view";
    private View rootView;
    private ContactsEntity thiscontact;
    private KeyListener emaillistener;
    private Drawable emailbackground;
    private KeyListener phonelistener;
    private Drawable phonebackground;
    private KeyListener noteslistener;
    private Drawable notesbackground;

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
        this.rootView = rootView;

        // fill in the values

        Bundle args = getArguments();
        ContactsEntity contact = getContact(args.getString("name"));
        this.thiscontact = contact;

        TextView contactTitle = rootView.findViewById(R.id.contact_title);
        contactTitle.setText(contact.getName());

        EditText email = rootView.findViewById(R.id.email);
        email.setText(contact.getEmail());
        emaillistener = email.getKeyListener();
        emailbackground = email.getBackground();
        email.setBackground(null);
        email.setKeyListener(null);

        EditText phone = rootView.findViewById(R.id.phone);
        phone.setText(contact.getPhone());
        phonelistener = phone.getKeyListener();
        phonebackground = phone.getBackground();
        phone.setKeyListener(null);
        phone.setBackground(null);

        EditText notes = rootView.findViewById(R.id.notes);
        notes.setText(contact.getNotes());
        noteslistener = notes.getKeyListener();
        notesbackground = notes.getBackground();
        notes.setKeyListener(null);
        notes.setBackground(null);


        // back button
        ((MainActivity) getActivity()).setToolbar("My Contacts", true);
        setHasOptionsMenu(true);

        return rootView;

    }

    /**
     * Fetches a single contact entity from the database, found by name
     *
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
        } catch (Exception e) {
        }

        return contact;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.edit, menu);
        editMenu = menu;
    }

    /**
     * Provides navigation for menu items; currenty only needed for navigation back to the
     * main contacts fragment.
     *
     * @param item the clicked menu item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // back button - go back
                ((MainActivity) getActivity()).switchFragment(Contacts.newInstance());
                return true;

            case R.id.details_edit: // edit button - edit contact details
                switchMode();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * switchMode toggles between viewing and editing the contact details.
     */
    public void switchMode() {

        if (this.mode.equals("view")) {
            editMenu.getItem(0).setIcon(R.drawable.tick);

            EditText email = rootView.findViewById(R.id.email);
            email.setText(thiscontact.getEmail());
            email.setKeyListener(emaillistener);
            email.setBackground(emailbackground);

            EditText phone = rootView.findViewById(R.id.phone);
            phone.setText(thiscontact.getPhone());
            phone.setKeyListener(phonelistener);
            phone.setBackground(phonebackground);

            EditText notes = rootView.findViewById(R.id.notes);
            notes.setText(thiscontact.getNotes());
            notes.setKeyListener(noteslistener);
            notes.setBackground(notesbackground);

            //TODO: make delete button appear

            this.mode = "edit";
            return;
        }

        if (this.mode.equals("edit")){
            editMenu.getItem(0).setIcon(R.drawable.edit);

            final EditText email = rootView.findViewById(R.id.email);
            email.setKeyListener(null);
            email.setBackground(null);

            final EditText phone = rootView.findViewById(R.id.phone);
            phone.setKeyListener(null);
            phone.setBackground(null);

            final EditText notes = rootView.findViewById(R.id.notes);
            notes.setKeyListener(null);
            notes.setBackground(null);

            this.mode = "view";
            return;
        }
    }

}
