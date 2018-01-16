package com.csbgroup.myphr;

import android.app.Instrumentation;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.csbgroup.myphr.database.AppDatabase;
import com.csbgroup.myphr.database.MedicineDao;
import com.csbgroup.myphr.database.MedicineEntity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class MedicineEntityTest {

    private MedicineDao medicineDao;
    private AppDatabase appDatabase;

    @Before
    public void createDb() {
        Context context = InstrumentationRegistry.getTargetContext();
        appDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        medicineDao = appDatabase.medicineDao();

        MedicineEntity medicineEntity = new MedicineEntity("Medicine",
                "Description", "Notes", true);
        medicineDao.insertAll(medicineEntity);
    }

    @After
    public void closeDb() throws IOException {
        appDatabase.close();
    }

    @Test
    public void createMedicineTest() throws Exception {
        MedicineEntity medicineEntity = new MedicineEntity("Test Medicine",
                "Description", "Notes", true);
        medicineDao.insertAll(medicineEntity);

        MedicineEntity me = medicineDao.getMedicine("Test Medicine");
        assertEquals(medicineEntity.getTitle(), me.getTitle());
    }

    @Test
    public void deleteMedicineTest() throws Exception {
        medicineDao.delete(medicineDao.getMedicine("Medicine"));
        assertEquals(null, medicineDao.getMedicine("Medicine"));
    }

    @Test
    public void insertMultipleMedicinesTest() throws Exception {
        List<String> titles = Arrays.asList("Med 1", "Med 2", "Med 3", "Med 4", "Medicine");

        List<MedicineEntity> medicines = new ArrayList<>();
        for (int i = 1; i < 5; i++)
            medicines.add(new MedicineEntity("Med " + i, null, null, false));
        medicineDao.insertAll(medicines.toArray(new MedicineEntity[medicines.size()]));

        assertEquals(titles, medicineDao.getAllTitles());
    }
}
