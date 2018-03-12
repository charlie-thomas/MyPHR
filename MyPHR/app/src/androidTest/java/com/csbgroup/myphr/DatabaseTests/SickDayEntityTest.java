package com.csbgroup.myphr.DatabaseTests;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.csbgroup.myphr.database.AppDatabase;
import com.csbgroup.myphr.database.SickDaysDao;
import com.csbgroup.myphr.database.SickDaysEntity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class SickDayEntityTest {

    private SickDaysDao sickDaysDao;
    private AppDatabase appDatabase;

    @Before
    public void createDb() {
        Context context = InstrumentationRegistry.getTargetContext();
        appDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        sickDaysDao = appDatabase.sickDaysDao();
    }

    @After
    public void closeDb() {
        appDatabase.close();
    }

    @Test
    public void createSickDayTest() throws Exception {
        SickDaysEntity sickDaysEntity = new SickDaysEntity("03/12/1997");
        sickDaysEntity.setUid(15);
        sickDaysDao.insert(sickDaysEntity);

        SickDaysEntity sd = sickDaysDao.getSickDay(15);
        assertEquals(sickDaysEntity.getDate(), sd.getDate());
    }

    @Test
    public void deleteInvestigationTest() throws Exception {
        SickDaysEntity sickDaysEntity = new SickDaysEntity("03/12/1997");
        sickDaysEntity.setUid(16);
        sickDaysDao.insert(sickDaysEntity);

        // Ensure the database contains the sick day to be deleted
        assertEquals(sickDaysEntity.getDate(), sickDaysDao.getSickDay(16).getDate());

        // Delete the sick day from the database and ensure the getSickDay query returns null
        sickDaysDao.delete(sickDaysDao.getSickDay(16));
        assertEquals(null, sickDaysDao.getSickDay(16));
    }

    @Test
    public void insertMultipleSickDaysTest() throws Exception {
        List<String> titles = Arrays.asList("Sick 1", "Sick 2", "Sick 3", "Sick 4");

        List<SickDaysEntity> sickDays = new ArrayList<>();
        for (int i = 1; i < 5; i++)
            sickDays.add(new SickDaysEntity("Sick " + i));
        sickDaysDao.insertAll(sickDays.toArray(new SickDaysEntity[sickDays.size()]));

        assertEquals(titles.size(), sickDaysDao.getAll().size());
    }

    @Test
    public void deleteAllSickDaysTest() throws Exception {
        for (int i = 1; i < 5; i++)
            sickDaysDao.insert(new SickDaysEntity("Sick " + i));

        // Ensure there are currently 4 sick days in the database
        assertEquals(4, sickDaysDao.getAll().size());

        // Delete all sick days and ensure there are 0 left
        sickDaysDao.deleteAll();
        assertEquals(0, sickDaysDao.getAll().size());
    }
}
