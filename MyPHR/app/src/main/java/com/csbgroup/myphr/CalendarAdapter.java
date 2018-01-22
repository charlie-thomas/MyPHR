package com.csbgroup.myphr;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        final CalendarEvent e = events.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.calendar_event_item, parent, false);
        }

        if (e != null) {
            TextView time = convertView.findViewById(R.id.time);
            TextView event = convertView.findViewById(R.id.event);

            if (time != null) time.setText(e.getTime());
            if (e.getEvent() != null) {
                event.setText(e.getEvent());
                event.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccent));

                event.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Fragment eventFrag = AppointmentsDetails.newInstance();
                        Bundle bundle = new Bundle();
                        bundle.putString("title", e.getEvent());
                        eventFrag.setArguments(bundle);

                        ((MainActivity) getContext()).switchFragment(eventFrag);
                    }
                });
            }
        }

        return convertView;
    }
}
