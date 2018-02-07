package com.csbgroup.myphr;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder> {

    private final List<List<CalendarEvent>> events;
    private Context ctx;

    public CalendarAdapter(List<List<CalendarEvent>> events) {
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
    public void onBindViewHolder(final CalendarViewHolder holder, int position) {

        final List<CalendarEvent> hours_events = events.get(position);

        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        holder.time.setText(hours_events.get(0).getTime());

        for (final CalendarEvent e : hours_events) {
            switch (e.getType()) {
                case "Empty":
                    break;
                case "Appointment":
                    if (holder.events.getChildCount() >= hours_events.size()) break;

                    TextView tv = (TextView) inflater.inflate(R.layout.calendar_event_list_item, null);

                    tv.setText(e.getEvent());
                    tv.setBackgroundColor(ContextCompat.getColor(ctx, R.color.colorAccent));

                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Fragment eventFrag = AppointmentsDetails.newInstance();
                            Bundle bundle = new Bundle();
                            bundle.putString("uid", String.valueOf(e.getUid()));
                            eventFrag.setArguments(bundle);

                            ((MainActivity) ctx).switchFragment(eventFrag);
                        }
                    });

                    holder.events.addView(tv);
                    break;
                case "Medicine":
                    if (holder.events.getChildCount() >= hours_events.size()) break;

                    TextView tv_med = (TextView) inflater.inflate(R.layout.calendar_event_list_item, null);

                    tv_med.setText(e.getEvent());
                    tv_med.setBackgroundColor(ContextCompat.getColor(ctx, R.color.colorAccentDark));

                    tv_med.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Fragment eventFrag = MedicineDetails.newInstance();
                            Bundle bundle = new Bundle();
                            bundle.putString("title", e.getEvent());
                            eventFrag.setArguments(bundle);

                            ((MainActivity) ctx).switchFragment(eventFrag);
                        }
                    });

                    holder.events.addView(tv_med);
                    break;
            }
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
