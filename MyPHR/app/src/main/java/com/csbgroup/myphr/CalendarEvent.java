package com.csbgroup.myphr;

public class CalendarEvent {

    private String time;
    private String date;
    private String event;
    private String type;

    public CalendarEvent(String time, String date, String event, String type) {
        this.time = time;
        this.date = date;
        this.event = event;
        this.type = type;
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

    public String getType() {
        return type;
    }
}
