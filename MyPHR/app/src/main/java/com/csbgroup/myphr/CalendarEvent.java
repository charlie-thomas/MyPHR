package com.csbgroup.myphr;

public class CalendarEvent {

    private int uid;
    private int hour;
    private String time;
    private String date;
    private String event;
    private String type;

    public CalendarEvent(int uid, int hour, String time, String date, String event, String type) {
        this.uid = uid;
        this.hour = hour;
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

    public int getUid() {
        return uid;
    }

    public int getHour() {
        return hour;
    }
}
