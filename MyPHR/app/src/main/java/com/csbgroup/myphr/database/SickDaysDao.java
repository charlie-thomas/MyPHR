package com.csbgroup.myphr.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface SickDaysDao {

    @Query("SELECT * FROM sickdaysentity")
    List<SickDaysEntity> getAll();

    @Query("SELECT * FROM sickdaysentity WHERE date LIKE :date")
    SickDaysEntity getSickDaysByDate(String date);

    @Insert
    long insert(SickDaysEntity sickDaysEntity);

    @Insert
    void insertAll(SickDaysEntity... sickDaysEntities);

    @Delete
    void delete(SickDaysEntity sickDaysEntity);

    @Query("DELETE FROM sickdaysentity")
    void deleteAll();
}
