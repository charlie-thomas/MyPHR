package com.csbgroup.myphr.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.ArrayList;

@Entity
public class StatisticsEntity {

    // Columns
    @NonNull
    @PrimaryKey
    private String unit;

    @ColumnInfo(name = "values")
    private ArrayList<String> values;


    // Constructor
    public StatisticsEntity(String unit, ArrayList<String> values) {
        this.unit = unit;
        this.values = values;
    }

    // Getters and Setters

    public String getUnit() {
        return unit;
    }

    public void setValues(ArrayList<String> values) {this.values = values;}

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public ArrayList<String> getValues(){ return values; }

    public void addValue(String value){ this.values.add(value);}

}
