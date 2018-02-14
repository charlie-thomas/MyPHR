package com.csbgroup.myphr.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.Date;
import java.util.List;

@Dao
public interface AppointmentsDao {

    @Query("SELECT * FROM appointmentsentity")
    List<AppointmentsEntity> getAll();

    @Query("SELECT title FROM appointmentsentity")
    List<String> getAllTitles();

    @Query("SELECT * FROM appointmentsentity WHERE uid LIKE :uid")
    AppointmentsEntity getAppointment(int uid);

    @Query("SELECT * FROM appointmentsentity WHERE date LIKE :date")
    List<AppointmentsEntity> getAppointmentByDate(String date);

    @Insert
    long insert(AppointmentsEntity appointmentsEntity);

    @Insert
    void insertAll(AppointmentsEntity... appointmentsEntities);

    @Update
    void update(AppointmentsEntity appointmentsEntity);

    @Delete
    void delete(AppointmentsEntity appointmentsEntity);

    @Query("DELETE FROM appointmentsentity")
    void deleteAll();
}
