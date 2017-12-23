package com.csbgroup.myphr.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class MedicineEntity {

    // Columns
    @NonNull
    @PrimaryKey
    private String title;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "notes")
    private String notes;

    @ColumnInfo(name = "reminders")
    private boolean reminders;

    @ColumnInfo(name = "reminder_type")
    private int reminder_type;

    @ColumnInfo(name = "remind_when")
    private int remind_when;

    // Constructor
    public MedicineEntity(String title, String description, String notes, boolean reminders) {
        this.title = title;
        this.description = description;
        this.notes = notes;
        this.reminders = reminders;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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