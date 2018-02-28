package com.csbgroup.myphr.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class SickDaysEntity {

    // Columns
    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo (name = "date")
    private String date;

    // Constructor
    public SickDaysEntity(String date) {
        this.date = date;
    }


    // Getters and Setters
    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
