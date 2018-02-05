package com.csbgroup.myphr;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class DateAdapter extends ArrayAdapter<CalendarEvent> {

    private List<CalendarEvent> events;

    public DateAdapter(Context context, List<CalendarEvent> events) {
        super(context, 0, events);
        this.events = events;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        CalendarEvent e = events.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.date_list_item, parent, false);
        }

        if (e != null) {
            TextView date = convertView.findViewById(R.id.date_item);
            TextView title = convertView.findViewById(R.id.title_item);

            if (date != null) date.setText(e.getDate());
            if (title != null) title.setText(e.getEvent());

            convertView.setTag(e.getUid());
        }

        return convertView;
    }
}
