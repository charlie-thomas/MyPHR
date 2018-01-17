package com.csbgroup.myphr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CalendarAdapter extends ArrayAdapter<CalendarEvent> {

    private ArrayList<CalendarEvent> events;

    public CalendarAdapter(Context context, ArrayList<CalendarEvent> events) {
        super(context, 0, events);
        this.events = events;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        CalendarEvent e = events.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.calendar_event_item, parent, false);
        }

        if (e != null) {
            TextView time = convertView.findViewById(R.id.time);
            TextView event = convertView.findViewById(R.id.event);

            if (time != null) time.setText(e.getTime());
            if (event != null) event.setText(e.getEvent());
        }

        return convertView;
    }
}
