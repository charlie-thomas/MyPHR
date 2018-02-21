package com.csbgroup.myphr;

import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.csbgroup.myphr.database.AppDatabase;
import com.csbgroup.myphr.database.MedicineEntity;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Medicine extends Fragment {

    private FloatingActionButton fab; // the add medicine fab

    public Medicine() {
        // Required empty public constructor
    }

    public static Medicine newInstance() {
        Medicine fragment = new Medicine();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // view set up
        View rootView = inflater.inflate(R.layout.fragment_medicine, container, false);
        ((MainActivity) getActivity()).setToolbar("My Medicine", false);
        setHasOptionsMenu(true);

        // fetch medicines entities from database
        List<MedicineEntity> medicines = getMedicines();
        if (medicines == null) return rootView;

        // Convert MedicineEntities into a map of their uid and titles
        List<Map.Entry<Integer, String>> medicine_map = new ArrayList<>();
        for (MedicineEntity me : medicines)
            medicine_map.add(new AbstractMap.SimpleEntry<>(me.getUid(), me.getTitle()));


        // display the medicines in list
        SimpleAdapter medicineAdapter = new SimpleAdapter(getActivity(), medicine_map);
        ListView listView = rootView.findViewById(R.id.medicine_list);
        listView.setAdapter(medicineAdapter);

        // switching to details fragment
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment details = MedicineDetails.newInstance();

                // Create a bundle to pass the medicine to the details fragment
                Bundle bundle = new Bundle();
                bundle.putString("uid", view.getTag().toString());
                details.setArguments(bundle);

                ((MainActivity) getActivity()).switchFragment(details);
            }
        });

        // fab action for adding medicine
        fab = rootView.findViewById(R.id.med_fab);
        buildDialog(fab);

        return rootView;
    }

    /**
     * getMedicines fetches the list of medicines from the database
     * @return the list of medicine entities
     */
    private List<MedicineEntity> getMedicines() {

        // Create a callable object for database transactions
        Callable callable = new Callable() {
            @Override
            public Object call() throws Exception {
                return AppDatabase.getAppDatabase(getActivity()).medicineDao().getAll();
            }
        };

        // Get a Future object of all the medicine titles
        ExecutorService service = Executors.newFixedThreadPool(2);
        Future<List<MedicineEntity>> result = service.submit(callable);

        // Create a list of the medicine names
        List<MedicineEntity> medicines = null;
        try {
            medicines = result.get();
        } catch (Exception e) {}

        return medicines;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.settings, menu);
    }

    /**
     *  Provides navigation for menu items; currently only needed for navigation to settings
     *  fragment.
     *  @param item is the clicked menu item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            ((MainActivity) getActivity()).switchFragment(MedicineSettings.newInstance());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * buildDialog builds the pop-up dialog for adding a new medicine
     * @param fab the floating action button which pulls up the dialog
     */
    public void buildDialog(FloatingActionButton fab) {

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // set up the dialog
                LayoutInflater inflater = getActivity().getLayoutInflater(); // get inflater
                View v = inflater.inflate(R.layout.add_medicine_dialog, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setView(v);

                // fetch the input values
                final EditText name = v.findViewById(R.id.med_name);
                final EditText description = v.findViewById(R.id.med_description);
                final EditText dose = v.findViewById(R.id.med_dose);
                final EditText notes = v.findViewById(R.id.med_notes);

                // add new medicine action
                builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // check that a name has been given
                        Boolean validName = true;
                        if (name.getText().toString().equals("")){
                            validName = false;
                        }

                        // format checks passed - add the new medicine to the database
                        if (validName){
                            new Thread(new Runnable(){
                                @Override
                                public void run(){
                                    AppDatabase db = AppDatabase.getAppDatabase(getActivity());
                                    MedicineEntity medicine = new MedicineEntity(name.getText().toString(),
                                            description.getText().toString(), dose.getText().toString(),
                                            notes.getText().toString(), false, true, false, null, "00:00");
                                    long uid = db.medicineDao().insert(medicine);

                                    // Move to details for new medicine
                                    Fragment newdetails = MedicineDetails.newInstance();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("uid", String.valueOf(uid));
                                    newdetails.setArguments(bundle);
                                    ((MainActivity)getActivity()).switchFragment(newdetails);
                                }
                            }).start();
                        }

                        // format checks failed - abort and show error message
                        else {
                            if (!validName){errorDialog("name");} // no name
                        }
                    }
                });

                // action for cancelling activity
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    /**
     * errorDialog is called when an invalid name is part of a medicine being added, it displays
     * an error message about the failure.
     * @param type is the type of error reported
     */
    public void errorDialog(String type){

        // set up the dialog
        LayoutInflater inflater = getActivity().getLayoutInflater(); // get inflater
        View v = inflater.inflate(R.layout.format_error, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);

        // specify error type
        final TextView errortype = v.findViewById(R.id.error_type);
        if (type.equals("name")){errortype.setText("YOU MUST PROVIDE A NAME");}

        final TextView errormessage = v.findViewById(R.id.error_message);
        errormessage.setText("Your medicine was not added.");

        // user dismiss message
        builder.setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
