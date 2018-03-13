package com.csbgroup.myphr.Calendar;

import com.prolificinteractive.materialcalendarview.*;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.Collection;
import java.util.HashSet;

/**
 * the EventDecorator class allows event dots to be shown under certain days on the
 * calendarMonth view
 */
public class EventDecorator implements DayViewDecorator {

    private int color;
    private final HashSet<CalendarDay> dates;

    public EventDecorator(int color, Collection<CalendarDay> dates) {
        this.color = color;
        this.dates = new HashSet<>(dates);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new DotSpan(6, color));
    }
}
