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

    private final ArrayList<CalendarEvent> events;

    public CalendarAdapter(Context context, ArrayList<CalendarEvent> events) {
        super(context, 0, events);
        this.events = events;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.calendar_event_item, parent, false);

            holder = new ViewHolder();

            holder.time = convertView.findViewById(R.id.time);
            holder.event = convertView.findViewById(R.id.event);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }



        final CalendarEvent e = events.get(position);

        if (e != null) {

            holder.time.setText(e.getTime());

            switch (e.getType()) {
                case "Empty":
                    break;
                case "Appointment":
                    holder.event.setText(e.getEvent());
                    holder.event.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccent));

                    holder.event.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Fragment eventFrag = AppointmentsDetails.newInstance();
                            Bundle bundle = new Bundle();
                            bundle.putString("title", e.getEvent());
                            eventFrag.setArguments(bundle);

                            ((MainActivity) getContext()).switchFragment(eventFrag);
                        }
                    });
                    break;
                case "Medicine":
                    holder.event.setText(e.getEvent());
                    holder.event.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccent));

                    holder.event.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Fragment eventFrag = MedicineDetails.newInstance();
                            Bundle bundle = new Bundle();
                            bundle.putString("title", e.getEvent());
                            eventFrag.setArguments(bundle);

                            ((MainActivity) getContext()).switchFragment(eventFrag);
                        }
                    });
                    break;
            }
        }

        return convertView;
    }

    private class ViewHolder {
        private TextView time;
        private TextView event;
    }
}
