package com.csbgroup.myphr;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class InvestigationsAdapter extends ArrayAdapter<InvestigationEvent> {

    private ArrayList<InvestigationEvent> events;

    public InvestigationsAdapter(Context context, ArrayList<InvestigationEvent> events) {
        super(context, 0, events);
        this.events = events;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        InvestigationEvent e = events.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.investigations_list_item, parent, false);
        }

        if (e != null) {
            TextView date = convertView.findViewById(R.id.invest_date);
            TextView title = convertView.findViewById(R.id.invest_title);

            if (date != null) date.setText(e.getDate());
            if (title != null) title.setText(e.getTitle());
        }

        return convertView;
    }
}
