package com.csbgroup.myphr.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

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

    @ColumnInfo(name = "remind_week")
    private boolean remind_week;

    @ColumnInfo(name = "remind_day")
    private boolean remind_day;

    @ColumnInfo(name = "remind_morning")
    private boolean remind_morning;

    // Constructor
    public AppointmentsEntity(String title, String location, String date, String time, String notes,
                              boolean reminders, int reminder_type, boolean remind_week, boolean remind_day,
                              boolean remind_morning) {
        this.title = title;
        this.location = location;
        this.date = date;
        this.time = time;
        this.notes = notes;
        this.reminders = reminders;
        this.reminder_type = reminder_type;
        this.remind_week = remind_week;
        this.remind_day = remind_day;
        this.remind_morning = remind_morning;
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
        this.date = date;
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    public boolean isRemind_week(){
        return remind_week;
    }

    public void setRemind_week(boolean remind_week){
        this.remind_week = remind_week;
    }

    public boolean isRemind_week() {
        return remind_week;
    }

    public void setRemind_week(boolean remind_week) {
        this.remind_week = remind_week;
    }

    public boolean isRemind_day() {
        return remind_day;
    }

    public void setRemind_day(boolean remind_day) {
        this.remind_day = remind_day;
    }

    public boolean isRemind_morning() {
        return remind_morning;
    }

    public void setRemind_morning(boolean remind_morning) {
        this.remind_morning = remind_morning;
    }

    public boolean isRemind_morning(){
        return remind_morning;
    }

    public void setRemind_morning(boolean remind_morning){
        this.remind_morning = remind_morning;
    }

}
