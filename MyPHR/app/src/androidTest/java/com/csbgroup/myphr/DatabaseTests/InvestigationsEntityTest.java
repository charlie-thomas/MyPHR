package com.csbgroup.myphr.DatabaseTests;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.csbgroup.myphr.database.AppDatabase;
import com.csbgroup.myphr.database.InvestigationsDao;
import com.csbgroup.myphr.database.InvestigationsEntity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class InvestigationsEntityTest {

    private InvestigationsDao investigationsDao;
    private AppDatabase appDatabase;

    @Before
    public void createDb() {
        Context context = InstrumentationRegistry.getTargetContext();
        appDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        investigationsDao = appDatabase.investigationDao();
    }

    @After
    public void closeDb() {
        appDatabase.close();
    }

    @Test
    public void createInvestigationTest() throws Exception {
        InvestigationsEntity investigationsEntity = new InvestigationsEntity("Investigation",
                "03/12/1997", "Notes");
        investigationsEntity.setUid(15);
        investigationsDao.insert(investigationsEntity);

        InvestigationsEntity ae = investigationsDao.getInvestigation(15);
        assertEquals(investigationsEntity.getTitle(), ae.getTitle());
    }

    @Test
    public void deleteInvestigationTest() throws Exception {
        InvestigationsEntity investigationsEntity = new InvestigationsEntity("Investigation",
                "03/12/1997", "Notes");
        investigationsEntity.setUid(16);
        investigationsDao.insert(investigationsEntity);

        // Ensure the database contains the investigations to be deleted
        assertEquals(investigationsEntity.getTitle(), investigationsDao.getInvestigation(16).getTitle());

        // Delete the investigations from the database and ensure the getInvestigation query returns null
        investigationsDao.delete(investigationsDao.getInvestigation(16));
        assertEquals(null, investigationsDao.getInvestigation(16));
    }

    @Test
    public void insertMultipleInvestigationsTest() throws Exception {
        List<String> titles = Arrays.asList("Invest 1", "Invest 2", "Invest 3", "Invest 4");

        List<InvestigationsEntity> investigations = new ArrayList<>();
        for (int i = 1; i < 5; i++)
            investigations.add(new InvestigationsEntity("Invest " + i, null, null));
        investigationsDao.insertAll(investigations.toArray(new InvestigationsEntity[investigations.size()]));

        assertEquals(titles, investigationsDao.getAllTitles());
    }

    @Test
    public void deleteAllInvestigationsTest() throws Exception {
        for (int i = 1; i < 5; i++)
            investigationsDao.insert(new InvestigationsEntity("Invest " + i, null, null));

        // Ensure there are currently 4 investigations in the database
        assertEquals(4, investigationsDao.getAll().size());

        // Delete all investigations and ensure there are 0 left
        investigationsDao.deleteAll();
        assertEquals(0, investigationsDao.getAll().size());
    }
}
