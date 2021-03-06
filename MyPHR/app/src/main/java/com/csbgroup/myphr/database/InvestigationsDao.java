package com.csbgroup.myphr.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface InvestigationsDao {

    @Query("SELECT * FROM investigationsentity")
    List<InvestigationsEntity> getAll();

    @Query("SELECT title FROM investigationsentity")
    List<String> getAllTitles();

    @Query("SELECT * FROM investigationsentity WHERE uid LIKE :uid")
    InvestigationsEntity getInvestigation(int uid);

    @Insert
    long insert(InvestigationsEntity investigationsEntity);

    @Insert
    void insertAll(InvestigationsEntity... investigationsEntities);

    @Update
    void update(InvestigationsEntity investigationsEntity);

    @Delete
    void delete(InvestigationsEntity investigationsEntity);

    @Query("DELETE FROM investigationsentity")
    void deleteAll();
}
