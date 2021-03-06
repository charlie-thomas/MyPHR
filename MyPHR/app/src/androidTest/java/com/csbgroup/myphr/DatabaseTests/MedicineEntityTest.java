package com.csbgroup.myphr.DatabaseTests;

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
    }

    @After
    public void closeDb() throws IOException {
        appDatabase.close();
    }

    @Test
    public void createMedicineTest() throws Exception {
        MedicineEntity medicineEntity = new MedicineEntity("Test Medicine",
                "Description", "Dose", "Notes", true, 0, true, false, "03/12/2017", "15:00");
        medicineDao.insert(medicineEntity);

        MedicineEntity me = medicineDao.getMedicineByTitle("Test Medicine");
        assertEquals(medicineEntity.getTitle(), me.getTitle());
    }

    @Test
    public void deleteMedicineTest() throws Exception {
        MedicineEntity medicineEntity = new MedicineEntity("Test Medicine",
                "Description", "Dose", "Notes", true, 0,
                true, true, "03/12/2017", "15:00");
        medicineDao.insert(medicineEntity);

        // Ensure that the medicine was added to the database before deleting it
        assertEquals(medicineEntity.getTitle(), medicineDao.getMedicineByTitle("Test Medicine").getTitle());

        // Delete the medicine from the database, and ensure that subsequent queries for the medicine
        // return null
        medicineDao.delete(medicineDao.getMedicineByTitle("Test Medicine"));
        assertEquals(null, medicineDao.getMedicineByTitle("Test Medicine"));
    }

    @Test
    public void insertMultipleMedicinesTest() throws Exception {
        List<String> titles = Arrays.asList("Med 1", "Med 2", "Med 3", "Med 4");

        List<MedicineEntity> medicines = new ArrayList<>();
        for (int i = 1; i < 5; i++)
            medicines.add(new MedicineEntity("Med " + i, null, null,null, false, 0,false, false, null, null ));
        medicineDao.insertAll(medicines.toArray(new MedicineEntity[medicines.size()]));

        assertEquals(titles, medicineDao.getAllTitles());
    }

    @Test
    public void getAllMedicinesTest() throws Exception {
        for (int i = 1; i < 5; i++)
            medicineDao.insert((new MedicineEntity("Med " + i, null, null,null, false, 0,false, false, null, null )));

        assertEquals(4, medicineDao.getAll().size());
    }

    @Test
    public void deleteAllMedicinesTest() throws Exception {
        for (int i = 1; i < 5; i++)
            medicineDao.insert(new MedicineEntity("Med " + i, null, null, null, false, 0,false, false, null, null));

        // Ensure there are currently 4 medicines in the database
        assertEquals(4, medicineDao.getAll().size());

        // Delete all medicines and ensure there are 0 left
        medicineDao.deleteAll();
        assertEquals(0, medicineDao.getAll().size());
    }

    @Test
    public void updateMedicineTest() throws Exception {
        MedicineEntity medicineEntity = new MedicineEntity("Test Medicine",
                "Description", "Dose", "Notes", true,0,
                true, true, "03/12/2017", "15:00");
        medicineEntity.setUid(132);
        medicineDao.insert(medicineEntity);

        // Ensure that the medicine was added to the database before updating it
        assertEquals(medicineEntity.getTitle(), medicineDao.getMedicine(132).getTitle());

        // Update the medicine from the database and ensure the getMedicine query returns the new version
        MedicineEntity updated = medicineDao.getMedicine(132);
        updated.setTitle("Updated Name");
        medicineDao.update(updated);

        assertEquals("Updated Name", medicineDao.getMedicine(132).getTitle());
    }
}
