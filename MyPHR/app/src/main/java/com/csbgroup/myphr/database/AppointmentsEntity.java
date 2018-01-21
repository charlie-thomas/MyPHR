package com.csbgroup.myphr.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class AppointmentsEntity {

    // Columns
    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "location")
    private String location;

    @ColumnInfo (name = "date")
    private String date;

    @ColumnInfo (name = "time")
    private String time;

    @ColumnInfo(name = "notes")
    private String notes;

    @ColumnInfo(name = "reminders")
    private boolean reminders;

    @ColumnInfo(name = "reminder_type")
    private int reminder_type;

    @ColumnInfo(name = "remind_when")
    private int remind_when;

    // Constructor
    public AppointmentsEntity(String title, String location, String date, String time, String notes,
                              boolean reminders) {
        this.title = title;
        this.location = location;
        this.date = date;
        this.time = time;
        this.notes = notes;
        this.reminders = reminders;
    }


    // Getters and Setters
    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = title;
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = title;
    }


    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }


    public boolean getReminders() {
        return reminders;
    }

    public void setReminders(boolean reminders) {
        this.reminders = reminders;
    }


    public int getReminder_type() {
        return reminder_type;
    }

    public void setReminder_type(int reminder_type) {
        this.reminder_type = reminder_type;
    }


    public int getRemind_when() {
        return remind_when;
    }

    public void setRemind_when(int remind_when) {
        this.remind_when = remind_when;
    }
}
