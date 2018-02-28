package com.csbgroup.myphr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.support.design.widget.FloatingActionButton;
import android.widget.TextView;

import com.csbgroup.myphr.database.AppDatabase;
import com.csbgroup.myphr.database.ContactsEntity;

import java.util.AbstractMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Contacts extends Fragment {

    private FloatingActionButton fab; //the add contact fab

    public Contacts() {
        // Required empty public constructor
    }

    public static Contacts newInstance() {
        Contacts fragment = new Contacts();
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // view set up
        View rootView = inflater.inflate(R.layout.fragment_contacts, container, false);
        ((MainActivity) getActivity()).setToolbar("My Contacts", false);
        setHasOptionsMenu(true);

        // fetch contacts entities from database
        List<ContactsEntity> conts = getContacts();
        if (conts == null) return rootView;

        // Convert ContactEntities into a map of their uid and names
        List<Map.Entry<Integer, String>> contacts_map = new ArrayList<>();
        for (ContactsEntity ce : conts)
            contacts_map.add(new AbstractMap.SimpleEntry<>(ce.getUid(), ce.getName()));

        // display the contacts in list
        SimpleAdapter contactsAdapter = new SimpleAdapter(getActivity(), contacts_map);
        ListView listView = rootView.findViewById(R.id.contacts_list);
        listView.setAdapter(contactsAdapter);

        // switching to details fragment
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment details = ContactDetails.newInstance();

                // Create a bundle to pass the contact to the details fragment
                Bundle bundle = new Bundle();
                bundle.putString("uid", view.getTag().toString());
                details.setArguments(bundle);

                ((MainActivity) getActivity()).switchFragment(details);
            }
        });

        // fab action for adding contact
        fab = rootView.findViewById(R.id.contact_fab);
        buildDialog(fab);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * getContacts fetches the list of contacts from the database
     *
     * @return the list of contact entities
     */
    private List<ContactsEntity> getContacts() {

        // Create a callable object for database transactions
        Callable callable = new Callable() {
            @Override
            public Object call() throws Exception {
                return AppDatabase.getAppDatabase(getActivity()).contactsDao().getAll();
            }
        };

        // Get a Future object of all the contact titles
        ExecutorService service = Executors.newFixedThreadPool(2);
        Future<List<ContactsEntity>> result = service.submit(callable);

        // Create a list of the contact names
        List<ContactsEntity> contacts = null;
        try {
            contacts = result.get();
        } catch (Exception e) {
        }

        return contacts;
    }

    /**
     * buildDialog builds the pop-up dialog for adding a new contact, with input format checking.
     *
     * @param fab the floating action button which pulls up the dialog
     */
    public void buildDialog(FloatingActionButton fab) {

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // set up the dialog
                LayoutInflater inflater = getActivity().getLayoutInflater(); // get inflater
                View v = inflater.inflate(R.layout.add_contact_dialog, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setView(v);

                // fetch the input values
                final EditText name = v.findViewById(R.id.contact_name);
                final EditText email = v.findViewById(R.id.contact_email);
                final EditText phone = v.findViewById(R.id.contact_phone);
                final EditText notes = v.findViewById(R.id.contact_notes);

                // add new contact action
                builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        // add the new contact to the database
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                AppDatabase db = AppDatabase.getAppDatabase(getActivity());
                                ContactsEntity contact = new ContactsEntity(name.getText().toString(),
                                        email.getText().toString(), phone.getText().toString(),
                                        notes.getText().toString());
                                long uid = db.contactsDao().insert(contact);

                                // Move to details for new contact
                                Fragment newdetails = ContactDetails.newInstance();
                                Bundle bundle = new Bundle();
                                bundle.putString("uid", String.valueOf(uid));
                                newdetails.setArguments(bundle);
                                ((MainActivity) getActivity()).switchFragment(newdetails);
                            }
                        }).start();
                    }
                });

                // cancel the add
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {}
                });

                final AlertDialog dialog = builder.create();
                dialog.show();

                // disable the add button until input conditions are met
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

                // ensure input name is valid
                name.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (name.getText().length() == 0) { // empty name
                            name.setError("Name cannot be empty"); // show error message
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        } else { // valid name
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                        }
                    }
                    @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                    @Override public void afterTextChanged(Editable editable) {}
                });
            }
        });
    }
}

