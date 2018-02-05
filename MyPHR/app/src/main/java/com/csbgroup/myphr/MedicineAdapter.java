package com.csbgroup.myphr;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class MedicineAdapter extends ArrayAdapter<CalendarEvent> {

    private List<CalendarEvent> events;

    public MedicineAdapter(Context context, List<CalendarEvent> events) {
        super(context, 0, events);
        this.events = events;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        final CalendarEvent e = events.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.todays_meds_list_item, parent, false);
        }

        if (e != null) {
            TextView time = convertView.findViewById(R.id.upcoming_time_med);
            TextView title = convertView.findViewById(R.id.upcoming_med_name);

            if (time != null) time.setText(e.getTime());
            if (title != null) title.setText(e.getEvent());

            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MedicineDetails frag = MedicineDetails.newInstance();

                    Bundle bundle = new Bundle();
                    bundle.putString("title", e.getEvent());
                    frag.setArguments(bundle);

                    ((MainActivity) getContext()).switchFragment(frag);
                }
            });
        }

        return convertView;
    }
}
