package com.csbgroup.myphr.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class InvestigationsEntity {

    // Columns
    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "date")
    private String date;

    @ColumnInfo(name = "notes")
    private String notes;

    // Constructor
    public InvestigationsEntity(String title, String date, String notes) {
        this.title = title;
        this.date = date;
        this.notes = notes;
    }

    // Getters and setters

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


    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }


    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }

}
