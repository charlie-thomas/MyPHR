package com.csbgroup.myphr.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.ArrayList;

@Entity
public class StatValueEntity {

    // Columns
    @NonNull
    @PrimaryKey
    private String value;

    @ColumnInfo(name = "date")
    private String date;

    @ColumnInfo (name = "centile")
    private String centile;


    // Constructor
    public StatValueEntity(String value,String date, String centile) {
        this.value = value;
        this.date =  date;
        this.centile = centile;

    }

    // Getters and Setters

    public String getValue() { return value; }
    public String getDate() { return date; }
    public String getCentile() { return centile; }

    public void setValue(String value) { this.value = value;}
    public void setDate(String date) { this.date = date;}
    public void setCentile(String centile){ this.centile = centile; }

}
