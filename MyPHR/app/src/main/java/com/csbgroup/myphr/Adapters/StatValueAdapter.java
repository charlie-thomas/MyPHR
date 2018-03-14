package com.csbgroup.myphr.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.csbgroup.myphr.MainActivity;
import com.csbgroup.myphr.R;
import com.csbgroup.myphr.Statistics.StatisticsDetailsList;
import com.csbgroup.myphr.Statistics.StatisticsSection;
import com.csbgroup.myphr.database.AppDatabase;
import com.csbgroup.myphr.database.StatValueEntity;
import com.csbgroup.myphr.database.StatisticsEntity;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import static com.csbgroup.myphr.Statistics.StatisticsDetailsList.updateHeightVelocity;

import static android.view.View.GONE;

/**
 * StatValueAdapter is a extended version of the ArrayAdapter used for displaying
 * all the different measurements within the application
 */
public class StatValueAdapter extends ArrayAdapter<StatValueEntity>{
    private Context mContext;
    int mResource;
    String mType;
    ArrayList<StatValueEntity> array;

    public StatValueAdapter(Context context, int resource, ArrayList<StatValueEntity> objects, String type) {
        super(context,resource,objects);
        mContext = context;
        mResource = resource;
        mType = type;
        array = objects;
    }


    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent){

        //get values at list position
        final String date = getItem(position).getDate();
        String value = getItem(position).getValue();
        final String centile = getItem(position).getCentile();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource,parent,false);

        //get each View in the adapter
        final TextView tvDate = convertView.findViewById(R.id.textView1);
        final TextView tvValue = convertView.findViewById(R.id.textView2);
        final TextView tvCentile = convertView.findViewById(R.id.textView3);
        final ImageButton deleteBtn = convertView.findViewById(R.id.delete_btn);

        //determine which ending will be required for the type of data
        tvDate.setText(date);
        if(!mType.equals("Body Mass Index (BMI)")) {
            String ending;
            switch(mType){
                case "Blood Pressure":
                    ending = " mmHg";
                    break;
                case "Head Circumference":
                    ending = "cm";
                    break;
                case "Height":
                    ending = "cm";
                    break;
                case "Weight":
                    ending = "kg";
                    break;
                case "Height Velocity":
                    ending = "cm/year";
                    break;
                default:
                    ending = "";
                    break;
            }
            tvValue.setText(value + ending); //concatenate to the end of the value
        } else{
            tvValue.setText(value);
        }


        //if the data doesn't have a centile then make centile view empty
        //otherwise determine and setText as 'value+ending' for centile textView
        if(centile==null || centile=="" || (!mType.equals("Weight") && !(mType.equals("Height")))){
            tvCentile.setText("");
        } else {
            switch (centile.substring(centile.length() - 1, centile.length())) {
                case "1":
                    tvCentile.setText(centile + "st" + " centile");
                    break;
                case "2":
                    tvCentile.setText(centile + "nd" +" centile");
                    break;
                case "3":
                    tvCentile.setText(centile + "rd"+ " centile");
                    break;
                default:
                    tvCentile.setText(centile + "th" + " centile");
                    break;
            }
        }

        // show delete buttons in editMode
        // and make invisible when not in editMode
        if(StatisticsDetailsList.isEditMode) {deleteBtn.setVisibility(View.VISIBLE);}
        else {deleteBtn.setVisibility(View.INVISIBLE);}

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // set up the view
                LayoutInflater inflater = LayoutInflater.from(mContext); // get inflater
                View v = inflater.inflate(R.layout.confirm_delete, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setView(v);
                final TextView message = v.findViewById(R.id.message);
                message.setText("Are you sure you want to delete this measurement?");

                // delete the measurement
                builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                AppDatabase db = AppDatabase.getAppDatabase(mContext);
                                final StatisticsEntity thisstat = getStats(mType);
                                thisstat.deleteValue(date);
                                db.statisticsDao().update(thisstat);
                                StatisticsDetailsList.valuesList = thisstat.getValues();

                                if(mType.equals("Height")){
                                    final StatisticsEntity heightvels = getStats("Height Velocity");
                                    ArrayList<StatValueEntity> newvels = updateHeightVelocity(getStats("Height").getValues());
                                    heightvels.getValues().clear();
                                    heightvels.getValues().addAll(newvels);
                                    db.statisticsDao().update(heightvels);
                                }
                            }
                        }).start();

                        array.remove(position);
                        notifyDataSetChanged();
                        notifyDataSetInvalidated();

                        // refresh to show no measurements image
                        if (array.size() == 0) {
                            Fragment dlist = StatisticsSection.newInstance();
                            Bundle bundle = new Bundle();
                            bundle.putString("title", mType);
                            dlist.setArguments(bundle);
                            ((MainActivity)mContext).switchFragment(dlist, true);
                        }
                    }
                });

                // cancel the delete
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {}
                });

                final AlertDialog dialog = builder.create();
                dialog.show();

                // set button colours
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.colorRed));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.colorRed));
            }

        });
        return convertView;
    }


    /**
     * getStats fetches the StatisticsEntity for the specified measurement type (e.g. weight/BMI...)
     * @param unit is the measurement type
     * @return the StatisticsEntity
     */
    public StatisticsEntity getStats(final String unit) {
        // Create a callable object for database transactions
        Callable callable = new Callable() {
            @Override
            public Object call() throws Exception {
                return AppDatabase.getAppDatabase(mContext).statisticsDao().getStatistic(unit);
            }
        };

        // Get a Future object of all the statistics titles
        ExecutorService service = Executors.newFixedThreadPool(2);
        Future<StatisticsEntity> result = service.submit(callable);

        // Create a list of the statistics names
        StatisticsEntity statistics = null;
        try {
            statistics = result.get();
        } catch (Exception e) {}

        return statistics;
    }
}