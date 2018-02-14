package com.csbgroup.myphr;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder> {

    private final List<CalendarEvent> events;
    private Context ctx;

    public CalendarAdapter(List<CalendarEvent> events) {
        setHasStableIds(true);
        this.events = events;
    }

    @Override
    public CalendarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ctx = parent.getContext();
        return new CalendarViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.calendar_event_item, parent, false));
    }

    @Override
    public void onBindViewHolder(CalendarViewHolder holder, int position) {

        final CalendarEvent e = events.get(position);
        holder.time.setText(e.getTime());

        switch (e.getType()) {
            case "Empty":
                break;
            case "Appointment":
                holder.event.setText(e.getEvent());
                holder.event.setBackgroundColor(ContextCompat.getColor(ctx, R.color.colorAccent));

                holder.event.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Fragment eventFrag = AppointmentsDetails.newInstance();
                        Bundle bundle = new Bundle();
                        bundle.putString("uid", String.valueOf(e.getUid()));
                        eventFrag.setArguments(bundle);

                        ((MainActivity) ctx).switchFragment(eventFrag);
                    }
                });
                break;
            case "Medicine":
                holder.event.setText(e.getEvent());
                holder.event.setBackgroundColor(ContextCompat.getColor(ctx, R.color.colorAccentDark));

                holder.event.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment eventFrag = MedicineDetails.newInstance();
                    Bundle bundle = new Bundle();
                    bundle.putString("title", e.getEvent());
                    eventFrag.setArguments(bundle);

                    ((MainActivity) ctx).switchFragment(eventFrag);
                    }
                });
                break;
            }
        }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return events != null ? events.size() : 0;
    }
}
