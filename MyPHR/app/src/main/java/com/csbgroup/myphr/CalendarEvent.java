package com.csbgroup.myphr;

public class CalendarEvent {

    private String time;
    private String date;
    private String event;

    public CalendarEvent(String time, String date, String event) {
        this.time = time;
        this.date = date;
        this.event = event;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public String getEvent() {
        return event;
    }
}
