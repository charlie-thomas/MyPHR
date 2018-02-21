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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.csbgroup.myphr.database.AppDatabase;
import com.csbgroup.myphr.database.ContactsEntity;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ContactDetails extends Fragment {

    private ContactsEntity thiscontact; // the contact we're viewing now

    private Menu editMenu;
    private String mode = "view";
    private View rootView;

    private KeyListener titlelistener, emaillistener, phonelistener, noteslistener;
    private Drawable titlebackground, emailbackground, phonebackground, notesbackground;

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
        ContactsEntity contact = getContact(Integer.valueOf(args.getString("uid")));
        this.thiscontact = contact;

        EditText contactTitle = rootView.findViewById(R.id.contact_title);
        contactTitle.setText(contact.getName());
        titlelistener = contactTitle.getKeyListener();
        titlebackground = contactTitle.getBackground();
        contactTitle.setBackground(null);
        contactTitle.setKeyListener(null);

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
     * @param uid is the primary key of the contact to be retrieved
     * @return the contact entity
     */
    private ContactsEntity getContact(final int uid) {

        // Create a callable object for database transactions
        Callable callable = new Callable() {
            @Override
            public Object call() throws Exception {
                return AppDatabase.getAppDatabase(getActivity()).contactsDao().getContact(uid);
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
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.edit, menu);
        editMenu = menu;
    }

    /**
     * Provides navigation/actions for menu items.
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

            EditText title = rootView.findViewById(R.id.contact_title);
            title.setText(thiscontact.getName());
            title.setBackground(titlebackground);
            title.setKeyListener(titlelistener);

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

            Button delete = rootView.findViewById(R.id.delete);
            delete.setVisibility(View.VISIBLE);

            this.mode = "edit";
            return;
        }

        if (this.mode.equals("edit")){
            editMenu.getItem(0).setIcon(R.drawable.edit);

            Button delete = rootView.findViewById(R.id.delete);
            delete.setVisibility(View.GONE);

            final EditText title = rootView.findViewById(R.id.contact_title);
            title.setKeyListener(null);
            title.setBackground(null);

            final EditText email = rootView.findViewById(R.id.email);
            email.setKeyListener(null);
            email.setBackground(null);

            final EditText phone = rootView.findViewById(R.id.phone);
            phone.setKeyListener(null);
            phone.setBackground(null);

            final EditText notes = rootView.findViewById(R.id.notes);
            notes.setKeyListener(null);
            notes.setBackground(null);

            // update the contact in the database
            new Thread(new Runnable() {
                @Override
                public void run() {
                    AppDatabase db = AppDatabase.getAppDatabase(getActivity());
                    thiscontact.setName(title.getText().toString());
                    thiscontact.setEmail(email.getText().toString());
                    thiscontact.setPhone(phone.getText().toString());
                    thiscontact.setNotes(notes.getText().toString());
                    db.contactsDao().update(thiscontact);

                    // refresh to get rid of keyboard
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.detach(ContactDetails.this).attach(ContactDetails.this).commit();
                }
            }).start();

            this.mode = "view";
            return;
        }
    }

}
