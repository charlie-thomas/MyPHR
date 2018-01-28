package com.csbgroup.myphr;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.csbgroup.myphr.database.StatValueEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JBizzle on 22/01/2018.
 */

public class StatValueAdapter extends ArrayAdapter<StatValueEntity> {
    private Context mContext;
    int mResource;

    /**
     * @param context
     * @param resource
     * @param objects
     */

    public StatValueAdapter(Context context, int resource, ArrayList<StatValueEntity> objects) {
        super(context,resource,objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        String date = getItem(position).getDate();
        String value = getItem(position).getValue();
        String centile = getItem(position).getCentile();

        StatValueEntity sve = new StatValueEntity(date,value,centile);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource,parent,false);

        TextView tvDate = (TextView) convertView.findViewById(R.id.textView1);
        TextView tvValue = (TextView) convertView.findViewById(R.id.textView2);
        TextView tvCentile = (TextView) convertView.findViewById(R.id.textView3);

        tvDate.setText("Date: "+date);
        tvValue.setText("Value: "+value);

        //if the centile doesn't exist then we remove the textview and double the height of the value's textview
        if(centile==null || centile==""){
            final float scale = getContext().getResources().getDisplayMetrics().density;
            int pixels = (int) (60 * scale + 0.5f);
            ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) tvCentile.getLayoutParams();
            params.height = pixels;
            tvValue.setLayoutParams(params);
            tvCentile.setVisibility(View.GONE);
        } else {
            tvCentile.setText("Centile: "+centile);
        }

        return convertView;
    }
}


