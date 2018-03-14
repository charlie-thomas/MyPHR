package com.csbgroup.myphr.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.csbgroup.myphr.Appointments.AppointmentsDetails;
import com.csbgroup.myphr.Calendar.CalendarEvent;
import com.csbgroup.myphr.Calendar.CalendarViewHolder;
import com.csbgroup.myphr.MainActivity;
import com.csbgroup.myphr.Medicine.MedicineDetails;
import com.csbgroup.myphr.R;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * CalendarAdapter is a extended version of the RecyclerView Adapter used for displaying
 * CalendarEvent objects on to the CalendarDay fragment
 */
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

        // Sort the each hours events so the are in order by time
        Collections.sort(hours_events, new Comparator<CalendarEvent>() {
            @Override
            public int compare(CalendarEvent o1, CalendarEvent o2) {
                return o1.getTime().replace(":", "").compareTo(o2.getTime().replace(":", ""));
            }
        });

        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Set the text for each hour
        String hourString = hours_events.get(0).getHour() + ":00";
        holder.time.setText(hourString);

        // Loop through each event for each hour
        for (final CalendarEvent e : hours_events) {

            // Depending on the type of the event, add it to the correct hour, with the correct design and onClickListener
            switch (e.getType()) {
                case "Empty":
                    break;
                case "Appointment":
                    if (holder.events.getChildCount() >= hours_events.size()) break;

                    LinearLayout ll_app = (LinearLayout) inflater.inflate(R.layout.todays_meds_list_item, null);
                    ll_app.setBackgroundColor(ContextCompat.getColor(ctx, R.color.colorAccent));

                    TextView time = ll_app.findViewById(R.id.upcoming_time_med);
                    time.setText(e.getTime());

                    TextView event = ll_app.findViewById(R.id.upcoming_med_name);
                    event.setText(e.getEvent());

                    ll_app.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Fragment eventFrag = AppointmentsDetails.newInstance();
                            Bundle bundle = new Bundle();
                            bundle.putString("uid", String.valueOf(e.getUid()));
                            eventFrag.setArguments(bundle);

                            BottomNavigationView bn = ((MainActivity) ctx).findViewById(R.id.bottom_nav);
                            bn.setSelectedItemId(R.id.appointments);

                            ((MainActivity) ctx).switchFragment(eventFrag, true);
                        }
                    });

                    holder.events.addView(ll_app);
                    break;
                case "Medicine":
                    if (holder.events.getChildCount() >= hours_events.size()) break;

                    LinearLayout ll_med = (LinearLayout) inflater.inflate(R.layout.todays_meds_list_item, null);
                    ll_med.setBackgroundColor(ContextCompat.getColor(ctx, R.color.colorAccentDark));

                    TextView time_med = ll_med.findViewById(R.id.upcoming_time_med);
                    time_med.setText(e.getTime());

                    TextView event_med = ll_med.findViewById(R.id.upcoming_med_name);
                    event_med.setText(e.getEvent());

                    ll_med.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Fragment eventFrag = MedicineDetails.newInstance();
                            Bundle bundle = new Bundle();
                            bundle.putString("uid", String.valueOf(e.getUid()));
                            eventFrag.setArguments(bundle);

                            BottomNavigationView bn = ((MainActivity) ctx).findViewById(R.id.bottom_nav);
                            bn.setSelectedItemId(R.id.medicine);

                            ((MainActivity) ctx).switchFragment(eventFrag, true);
                        }
                    });

                    holder.events.addView(ll_med);
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
