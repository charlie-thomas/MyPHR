package com.csbgroup.myphr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.csbgroup.myphr.database.AppDatabase;
import com.csbgroup.myphr.database.MedicineEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Medicine extends Fragment {

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

        View rootView = inflater.inflate(R.layout.fragment_medicine, container, false);

        ((MainActivity) getActivity()).setToolbar("My Medicine");
        setHasOptionsMenu(true);


        List<MedicineEntity> medicines = getMedicines();
        if (medicines == null) return rootView;

        List<String> medicineTitles = new ArrayList<>();
        for (MedicineEntity me : medicines) {
            medicineTitles.add(me.getTitle());
        }

        ArrayAdapter<String> medicineAdapter = new ArrayAdapter<>(
                getActivity(),
                R.layout.simple_list_item,
                medicineTitles);

        ListView listView = rootView.findViewById(R.id.medicine_list);
        listView.setAdapter(medicineAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment details = MedicineDetails.newInstance();

                // Create a bundle to pass the medicine name to the details fragment
                Bundle bundle = new Bundle();
                bundle.putString("title", parent.getAdapter().getItem(position).toString());
                details.setArguments(bundle);

                ((MainActivity) getActivity()).switchFragment(details);
            }
        });

        return rootView;
    }

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

        // Create a list of the appointment names
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            ((MainActivity) getActivity()).switchFragment(MedicineSettings.newInstance());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
