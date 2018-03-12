package com.csbgroup.myphr.Calendar;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.csbgroup.myphr.R;

/* Class to aid the CalendarDay view, and ensure items do not repeat */
public class CalendarViewHolder extends RecyclerView.ViewHolder {

    public TextView time;
    public LinearLayout events;

    public CalendarViewHolder(View itemView) {
        super(itemView);

        this.time = itemView.findViewById(R.id.cal_time);
        this.events = itemView.findViewById(R.id.cal_event);
    }
}
