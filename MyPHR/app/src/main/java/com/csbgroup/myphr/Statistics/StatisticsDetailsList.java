package com.csbgroup.myphr.Statistics;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import com.csbgroup.myphr.MainActivity;
import com.csbgroup.myphr.R;
import com.csbgroup.myphr.Adapters.StatValueAdapter;
import com.csbgroup.myphr.database.AppDatabase;
import com.csbgroup.myphr.database.StatValueEntity;
import com.csbgroup.myphr.database.StatisticsEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static android.view.View.GONE;

public class StatisticsDetailsList extends Fragment {

    FloatingActionButton fab; // add measurement fab

    public static ListView listView;
    public static StatValueAdapter adapter;
    public static boolean isEditMode = false;
    public static View rootView;
    public static String type;
    static Menu editMenu;

    //This formatter is for changing the string entered in form "dd/MM/yyyy" into a Java Date type
    static final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

    // format checking booleans
    private boolean validMeasurement = false;
    private boolean validDate = false;
    private boolean validCentile = true;
    private boolean sameDate = false;
    public static ArrayList<StatValueEntity> valuesList;

    public StatisticsDetailsList() {} // Required empty public constructor

    public static StatisticsDetailsList newInstance() {
        StatisticsDetailsList fragment = new StatisticsDetailsList();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // view set up
        rootView = inflater.inflate(R.layout.fragment_statistics_details_list, container, false);
        ((MainActivity) getActivity()).setToolbar("", true);
        setHasOptionsMenu(true);
        isEditMode = false;

        Bundle args = getArguments();
        type = args.getString("title");

        //currentStat is  the StatisticsEntity for the current statistics page (e.g weight, height etc)
        final StatisticsEntity currentStat = getStats(args.getString("title", "Statistics"));

        //valuesList is the list of all the entity's in currentStat. Each contains a date, value and centile.
        valuesList =  currentStat.getValues();

        // order valuesList by oldest date first so that graph plots old -> new
        Collections.sort(valuesList, new Comparator<StatValueEntity>(){
            @Override
            public int compare(StatValueEntity t1, StatValueEntity t2) {
                try {
                    return formatter.parse(t1.getDate()).compareTo(formatter.parse(t2.getDate()));
                }
                catch (ParseException e) {e.printStackTrace();}
                return 0;
            }
        });

        // reorder valuesList by newest date first for list view
        Collections.reverse(valuesList);

        listView = rootView.findViewById(R.id.statistics_graph_list);
        /*The listView uses a custom adapter which uses an xml to print each list item
        Format located in stat_list_adapter.xml
        Listview is formatted in StatValueAdpater.java */
        adapter = new StatValueAdapter(getActivity(),R.layout.stat_list_adapter, valuesList,type);
        listView.setAdapter(adapter);

        // Show "No measurements" message if measurements empty
        LinearLayout no_stats = rootView.findViewById(R.id.no_stats);
        no_stats.setVisibility(View.INVISIBLE);
        if (listView.getAdapter().getCount() == 0) no_stats.setVisibility(View.VISIBLE);

        // fab action for adding measurement
        fab = rootView.findViewById(R.id.s_fab);
        buildDialog(fab, type, args);

        return rootView;
    }

    /**
     * getStats fetches the statisticsEntity for a given measurement type from the database.
     * @param unit is the measurement type (height/weight/BMI...)
     * @return the requested statisticsEntity
     */
    public StatisticsEntity getStats(final String unit) {
        // Create a callable object for database transactions
        Callable callable = new Callable() {
            @Override
            public Object call() throws Exception {
                return AppDatabase.getAppDatabase(getActivity()).statisticsDao().getStatistic(unit);
            }
        };

        // Get a Future object of all the statistics titles
        ExecutorService service = Executors.newFixedThreadPool(2);
        Future<StatisticsEntity> result = service.submit(callable);

        // Create a list of the statistics names
        StatisticsEntity statistics = null;
        try {
            statistics = result.get();
        }
        catch (Exception e) {}
        return statistics;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(!type.equals("Height Velocity")) inflater.inflate(R.menu.edit, menu); //edit button if type isn't Height Velocity
        editMenu = menu;
    }

    /**
     * onOptionsItemSelected provides navigation/actions for menu items.
     * @param item is the clicked menu item
     * @return super.onOptionsItemSelected(item)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home: // back button - go back
                ((MainActivity) getActivity()).switchFragment(Statistics.newInstance(), false);
                return true;

            case (R.id.details_edit): // edit button - edit measurements list
                if (!type.equals("Height Velocity")) { // don't want user be able to edit Height Velocity
                    TabHost tabhost = getActivity().findViewById(R.id.tabHost);
                    View thisfab = rootView.findViewById(R.id.s_fab);

                    if(listView.getChildCount()>0) { // only edit if there are values to edit
                        if (tabhost.getCurrentTab() == 0) { // list view
                            if (isEditMode) { // if already editing then stop
                                editMenu.getItem(0).setIcon(R.drawable.edit);
                                isEditMode = false;
                                adapter.notifyDataSetChanged();
                                thisfab.setVisibility(View.VISIBLE);
                            } else { // if not already editing, then begin
                                editMenu.getItem(0).setIcon(R.drawable.tick);
                                isEditMode = true;
                                adapter.notifyDataSetChanged();
                                thisfab.setVisibility(View.GONE);
                            }
                        } else { // graph view, change tab to list and activate edit mode
                            tabhost.setCurrentTab(0);
                            isEditMode = true;
                            editMenu.getItem(0).setIcon(R.drawable.tick);
                            adapter.notifyDataSetChanged();
                            thisfab.setVisibility(View.GONE);
                        }
                    }
                }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * buildDialog builds the pop-up dialog for adding a new appointment, with input format checking.
     * @param fab the floating action button which pulls up the dialog
     * @param type is the type of measurement being added
     * @param args is the bundled args
     */
    public void buildDialog(FloatingActionButton fab, final String type, final Bundle args) {

        // hide fab for height velocity
        if (type.equals("Height Velocity")) {
            fab.setVisibility(GONE);
            return;
        }

        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                View v;
                final EditText cent;
                final EditText diastolic;
                LayoutInflater inflater = getActivity().getLayoutInflater(); // get inflater

                // set up the appropriate dialog view
                if (type.equals("Height") || type.equals("Weight")) {
                    v = inflater.inflate(R.layout.add_measurement_centile, null);
                    diastolic = null;
                    cent = v.findViewById(R.id.centile);
                } else if (type.equals("Blood Pressure")) {
                    v = inflater.inflate(R.layout.add_measurement_bp, null);
                    diastolic = v.findViewById(R.id.bp_diastolic);
                    cent = null;
                } else {
                    v = inflater.inflate(R.layout.add_measurement_basic, null);
                    diastolic = null;
                    cent = null;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setView(v);

                // set measurement specific texts
                final TextView title = v.findViewById((R.id.dialog_title));
                final EditText measurement = v.findViewById(R.id.measurement);

                // set the title and hint
                if (type.equals("Body Mass Index (BMI)")) {
                    title.setText("Add a New BMI");
                    measurement.setHint("BMI");
                } else if (!type.equals("Blood Pressure")){
                    title.setText("Add a New " + type);
                    measurement.setHint(type);
                } else {
                    title.setText("Add a New Blood Pressure");
                }

                // fetch the input values (measurement already fetched above ^)
                final EditText day = v.findViewById(R.id.meas_DD);
                final EditText month = v.findViewById(R.id.meas_MM);
                final EditText year = v.findViewById(R.id.meas_YYYY);

                // hide the invisible EditText
                EditText date_error = v.findViewById(R.id.date_error);
                date_error.setKeyListener(null);
                date_error.setBackground(null);

                // add a new measurement action
                builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        // join date into one string
                        final String fullDate = day.getText().toString() + "/" + month.getText().toString()
                                + "/" + year.getText().toString();

                        // concatenate blood pressure measurments
                        final String mmnt;
                        if(type.equals("Blood Pressure")){mmnt = measurement.getText().toString() +"/"+ diastolic.getText().toString();}
                        else {mmnt = measurement.getText().toString();}

                        // fetch centile or assign null appropriately
                        final String centile;
                        if (type.equals("Height") || type.equals("Weight")) {centile = cent.getText().toString();}
                        else {centile = null;}

                        //Add the new measurement to the database
                        new Thread(new Runnable() {
                                @Override
                                public void run() {
                                AppDatabase db = AppDatabase.getAppDatabase(getActivity());
                                final StatisticsEntity thisstat = getStats(type);
                                thisstat.addValue(mmnt, fullDate, centile);
                                db.statisticsDao().update(thisstat);

                                //if type is height, then we also calculate the height velocities and add these to the database
                                if(type.equals("Height")){
                                    final StatisticsEntity heightvels = getStats("Height Velocity");
                                    ArrayList<StatValueEntity> newvels = updateHeightVelocity(getStats("Height").getValues());
                                    heightvels.getValues().clear();
                                    heightvels.getValues().addAll(newvels);
                                    db.statisticsDao().update(heightvels);
                                }

                                // refresh the view
                                Fragment details = StatisticsSection.newInstance();
                                Bundle bundle = new Bundle();
                                bundle.putString("title", args.getString("title", "Measurements"));
                                details.setArguments(bundle);
                                ((MainActivity) getActivity()).switchFragment(details, true);
                                }
                        }).start();
                    }
                });

                // cancel the add
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                });

                final AlertDialog dialog = builder.create();
                dialog.show();

                // disable the add button until input conditions are met
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

                // check user input
                inputChecking(v, type, diastolic, dialog);
            }
        });
    }

    /**
     * updateHeightVelocity updates the height velocity calculations.
     * @param orderedHeight is the list of height measurements as StatValueEntities
     * @return the height velocity values
     */
    public static ArrayList<StatValueEntity> updateHeightVelocity(ArrayList<StatValueEntity> orderedHeight){

        Collections.sort(orderedHeight, new Comparator<StatValueEntity>() {
            @Override
            public int compare(StatValueEntity t1, StatValueEntity t2) {
                try {
                    return formatter.parse(t1.getDate()).compareTo(formatter.parse(t2.getDate()));
                }
                catch (ParseException e) {e.printStackTrace();}
                return 0;
            }
        });

        double heightChanged;
        long diff;
        double heightVelocity;
        float days = 0;
        final ArrayList<StatValueEntity> heightVels = new ArrayList<>();

        for(int i=1; i<orderedHeight.size(); i++) {
            heightChanged = Double.parseDouble(orderedHeight.get(i).getValue()) - Double.parseDouble(orderedHeight.get(i-1).getValue());
            try {
                diff = formatter.parse(orderedHeight.get(i).getDate()).getTime() -
                        formatter.parse(orderedHeight.get(i-1).getDate()).getTime();
                days = (diff / (1000 * 60 * 60 * 24));

            } catch (ParseException e) {
                e.printStackTrace();
            }
            heightVelocity = Math.round(heightChanged / (days / 365));
            heightVels.add(new StatValueEntity(Double.toString(heightVelocity), orderedHeight.get(i).getDate(), null));
        }
        return heightVels;
    }

    /**
     * inputChecking checks the user input when adding a new measurement, the add button is disabled
     * until all format conditions are met.
     * @param v is the view for the add measurement dialog
     * @param t is the type of measurement we're adding
     * @param d is the diastolic value if adding blood pressure
     * @param ad is the new measurement alertdialog
     */
    public void inputChecking(View v, String t, EditText d, AlertDialog ad){

        final EditText measurement = v.findViewById(R.id.measurement);
        final EditText day = v.findViewById(R.id.meas_DD);
        final EditText month = v.findViewById(R.id.meas_MM);
        final EditText year = v.findViewById(R.id.meas_YYYY);
        final EditText centile = v.findViewById(R.id.centile);
        final EditText date_error = v.findViewById(R.id.date_error);
        final EditText diastolic = d;
        final String type = t;
        final AlertDialog dialog = ad;

        // ensure measurement is present
        measurement.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String mmnt;
                if(type.equals("Blood Pressure")){
                    String sys = measurement.getText().toString();
                    String dias = diastolic.getText().toString();
                    mmnt = sys + "/" + dias;

                    if (sys.equals("")){validMeasurement = false; measurement.setError("Systolic cannot be empty");}
                    if (dias.equals("")){validMeasurement = false; diastolic.setError("Diastolic cannot be empty");}
                }
                else {mmnt = measurement.getText().toString();}

                if (mmnt.equals("")){validMeasurement = false; measurement.setError("Measurement cannot be empty");}
                else {validMeasurement = true;}

                // disable/enable add button following format checks
                if (validMeasurement && validDate && validCentile) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);}
                else {dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);}

            }
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void afterTextChanged(Editable editable) {}
        });

        // ensure measurement day is valid
        day.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (checkFullDate(day, month, year)){validDate = true; date_error.setError(null);} // valid date
                else {
                    validDate = false;
                    if(sameDate) {
                        date_error.setError("Date already exists (DD MM YYYY)");// invalid date due to duplicate
                    } else {
                        date_error.setError("Invalid date (DD MM YYYY)");// invalid date
                    }
                }

                // disable/enable add button following format checks
                if (validMeasurement && validDate && validCentile) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }
                else {dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);}

                if (day.getText().toString().length() == 2) {month.requestFocus();}
            }
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void afterTextChanged(Editable editable) {}
        });

        // ensure measurement month is valid
        month.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (checkFullDate(day, month, year)){validDate = true; date_error.setError(null);} // valid date
                else {
                    validDate = false;
                    if(sameDate) {
                        date_error.setError("Date already exists (DD MM YYYY)");// invalid date due to duplicate
                    } else {
                        date_error.setError("Invalid date (DD MM YYYY)");// invalid date
                    }
                }

                // disable/enable add button following format checks
                if (validMeasurement && validDate && validCentile) {dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);}
                else {dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);}

                if (month.getText().toString().length() == 2) {year.requestFocus();}
            }
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void afterTextChanged(Editable editable) {}
        });

        // ensure measurement year is valid
        year.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (checkFullDate(day, month, year)){validDate = true; date_error.setError(null);} // valid date
                else {
                    validDate = false;
                    if(sameDate) {
                        date_error.setError("Date already exists (DD MM YYYY)");// invalid date due to duplicate
                    } else {
                        date_error.setError("Invalid date (DD MM YYYY)");// invalid date
                    }
                }

                // disable/enable add button following format checks
                if (validMeasurement && validDate && validCentile) {dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);}
                else {dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);}

                if (year.getText().toString().length() == 4 && (type.equals("Height") || type.equals("Weight"))) {centile.requestFocus();}
            }
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void afterTextChanged(Editable editable) {}
        });

        // ensure measurement centile is valid
        if (type.equals("Height") || type.equals("Weight")){
            centile.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    String cent;
                    if (type.equals("Height") || type.equals("Weight")){
                        cent = centile.getText().toString();
                        if (!cent.equals("")){
                            int centileint = Integer.parseInt(cent); // convert to int for checks
                            if (centileint < 1 || centileint > 100) {
                                validCentile = false;
                                centile.setError("Invalid centile (1-100)");
                            } else {validCentile = true;}
                        } else {validCentile = true;}
                    }

                    // disable/enable add button following format checks
                    if (validMeasurement && validDate && validCentile) {dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);}
                    else {dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);}
                }
                @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                @Override public void afterTextChanged(Editable editable) {}
            });
        }
    }

    /**
     * checkFullDate checks the validity of the full date across the three fields in the add dialog
     * whenever any of them is changed.
     * @param et1 is the day.
     * @param et2 is the month.
     * @param et3 is the year.
     * @return
     */
    public boolean checkFullDate(EditText et1, EditText et2, EditText et3){

        boolean validDate = true;

        // join date into one string
        final String date = et1.getText().toString() + "/" + et2.getText().toString() + "/" + et3.getText().toString();

        if (date.equals("//")) {validDate = false;} // no date given
        else if (date.length() != 10) {validDate = false;} // incomplete date
        else {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date d = sdf.parse(date);
                if (!date.equals(sdf.format(d))){
                    validDate = false;
                } else {
                    sameDate = false;
                    for (int i = 0; i < valuesList.size(); i++) {
                        if (valuesList.get(i).getDate().equals(date)) {
                            validDate = false;
                            sameDate = true;
                        }
                    }
                }
            } catch (ParseException e) {e.printStackTrace();}
        }
        return validDate;
    }
}