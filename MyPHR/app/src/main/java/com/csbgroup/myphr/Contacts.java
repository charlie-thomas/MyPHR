package com.csbgroup.myphr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.support.design.widget.FloatingActionButton;
import com.csbgroup.myphr.database.AppDatabase;
import com.csbgroup.myphr.database.StatisticsEntity;

import java.util.List;
import java.util.ArrayList;

public class Contacts extends Fragment {

    FloatingActionButton fab; //the add contact fab

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

        View rootView = inflater.inflate(R.layout.fragment_contacts, container, false);

        ((MainActivity) getActivity()).setToolbar("My Contacts", false);
        setHasOptionsMenu(true);

        List<String> contacts = new ArrayList<String>(){{
            add("Dr. A"); add("Dr. B"); add("Dr. C"); add("Ms. D"); add("Mr. E");}};

        ArrayAdapter<String> contactsAdapter = new ArrayAdapter<>(
                getActivity(),
                R.layout.simple_list_item,
                contacts);

        ListView listView = rootView.findViewById(R.id.contacts_list);
        listView.setAdapter(contactsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment details = ContactDetails.newInstance();

                // Create a bundle to pass the medicine name to the details fragment
                Bundle bundle = new Bundle();
                bundle.putString("title", parent.getAdapter().getItem(position).toString());
                details.setArguments(bundle);

                ((MainActivity) getActivity()).switchFragment(details);
            }
        });

        // fab action for adding contact
        fab = (android.support.design.widget.FloatingActionButton) rootView.findViewById(R.id.contact_fab);
        buildDialog(fab);


        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * buildDialog builds the pop-up dialog for adding a new contact
     * @param fab the floating action button which pulls up the dialog
     */
    public void buildDialog(FloatingActionButton fab){

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater inflater = getActivity().getLayoutInflater(); // get inflater

                // build the dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setView(inflater.inflate(R.layout.add_contact_dialog,null));

                // action for confirming add
                builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO: database activity
                    }
                });

                // action for cancelling activity
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }


}
