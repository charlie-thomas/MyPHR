package com.csbgroup.myphr.Medicine;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.csbgroup.myphr.MainActivity;
import com.csbgroup.myphr.R;
import com.csbgroup.myphr.Adapters.SimpleAdapter;
import com.csbgroup.myphr.database.AppDatabase;
import com.csbgroup.myphr.database.MedicineEntity;

import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Medicine extends Fragment {

    private FloatingActionButton fab; // add medicine fab

    public Medicine() {} // Required empty public constructor

    public static Medicine newInstance() {
        return new Medicine();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // view set up
        View rootView = inflater.inflate(R.layout.fragment_medicine, container, false);
        ((MainActivity) getActivity()).setToolbar("My Medication", false);
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

                ((MainActivity) getActivity()).switchFragment(details, true);
            }
        });

        // display no medications message when meds empty
        LinearLayout nomeds = rootView.findViewById(R.id.no_meds);
        nomeds.setVisibility(View.INVISIBLE);
        if (listView.getAdapter().getCount() == 0) nomeds.setVisibility(View.VISIBLE);

        // fab action for adding medicine
        FloatingActionButton fab = rootView.findViewById(R.id.med_fab);
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
        } catch (Exception e) {
            e.printStackTrace();
        }

        return medicines;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * buildDialog builds the pop-up dialog for adding a new medicine, with input format checking.
     * @param fab the floating action button which pulls up the dialog
     */
    public void buildDialog(FloatingActionButton fab) {

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // set up the dialog
                LayoutInflater inflater = getActivity().getLayoutInflater(); // get inflater
                @SuppressLint("InflateParams") View v = inflater.inflate(R.layout.add_medicine_dialog, null);
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

                        // Add the new medicine to the database
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                AppDatabase db = AppDatabase.getAppDatabase(getActivity());
                                @SuppressLint("SimpleDateFormat") MedicineEntity medicine = new MedicineEntity(name.getText().toString(),
                                        description.getText().toString(), dose.getText().toString(),
                                        notes.getText().toString(), false, 0,true, false,
                                        new SimpleDateFormat("dd/MM/yyyy").format(new Date()), //today's date
                                        "00:00");
                                long uid = db.medicineDao().insert(medicine);

                                // Move to details for new medicine
                                Fragment newdetails = MedicineDetails.newInstance();
                                Bundle bundle = new Bundle();
                                bundle.putString("uid", String.valueOf(uid));
                                newdetails.setArguments(bundle);
                                ((MainActivity) getActivity()).switchFragment(newdetails, true);
                            }
                        }).start();
                    }
                });

                // cancel the add
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {}
                });

                final AlertDialog dialog = builder.create();
                dialog.show();

                // disable the add button until input conditions are met
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

                // check user input
                inputChecking(name, dialog);
            }
        });
    }

    /**
     * inputChecking checks the user input when adding a new medication, the add button is disabled
     * until all format conditions are met.
     * @param et is the medication name, which must not be empty.
     * @param d is the new medication alertdialog.
     */
    public void inputChecking(EditText et, AlertDialog d){

        final EditText name = et;
        final AlertDialog dialog = d;

        // ensure medication name is valid
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
}
