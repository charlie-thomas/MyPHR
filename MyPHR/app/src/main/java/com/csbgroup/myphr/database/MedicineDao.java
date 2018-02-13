package com.csbgroup.myphr.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface MedicineDao {

    @Query("SELECT * FROM medicineentity")
    List<MedicineEntity> getAll();

    @Query("SELECT * FROM medicineentity WHERE uid LIKE :uid")
    MedicineEntity getMedicine(int uid);

    @Query("SELECT * FROM medicineentity WHERE title LIKE :title")
    MedicineEntity getMedicineByTitle(String title);

    @Query("SELECT title FROM medicineentity")
    List<String> getAllTitles();

    @Insert
    void insertAll(MedicineEntity... medicineEntities);

    @Update
    void update(MedicineEntity medicineEntity);

    @Delete
    void delete(MedicineEntity medicineEntity);

    @Query("DELETE FROM medicineentity")
    void deleteAll();
}
