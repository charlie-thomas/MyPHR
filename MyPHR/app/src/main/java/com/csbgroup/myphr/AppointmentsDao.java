package com.csbgroup.myphr;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface AppointmentsDao {

    @Query("SELECT * FROM appointmentsentity")
    List<AppointmentsEntity> getAll();

    @Insert
    void insertAll(List<AppointmentsEntity> appointmentsEntities);

    @Update
    void update(AppointmentsEntity appointmentsEntity);

    @Delete
    void delete(AppointmentsEntity appointmentsEntity);
}
