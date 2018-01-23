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
    private ArrayList<StatValueEntity> values;


    // Constructor
    public StatisticsEntity(String unit, ArrayList<StatValueEntity> values) {
        this.unit = unit;
        this.values = values;
    }

    // Getters and Setters

    public String getUnit() {
        return unit;
    }

    public void setValues(ArrayList<StatValueEntity> values) {this.values = values;}

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public ArrayList<StatValueEntity> getValues(){ return values; }

    public void addValue(String value,String date,String centile){
        StatValueEntity sve = new StatValueEntity(value,date,centile);
        this.values.add(sve);}

}
