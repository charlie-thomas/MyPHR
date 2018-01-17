package com.csbgroup.myphr;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.csbgroup.myphr.database.AppDatabase;
import com.csbgroup.myphr.database.StatisticsDao;
import com.csbgroup.myphr.database.StatisticsEntity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class StatisticsEntityTest {

    private StatisticsDao statisticsDao;
    private AppDatabase appDatabase;

    @Before
    public void createDb() {
        Context context = InstrumentationRegistry.getTargetContext();
        appDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        statisticsDao = appDatabase.statisticsDao();
    }

    @After
    public void closeDb() {
        appDatabase.close();
    }

    @Test
    public void createStatisticTest() throws Exception {
        StatisticsEntity statisticsEntity = new StatisticsEntity("Unit", null);
        statisticsDao.insertAll(statisticsEntity);

        StatisticsEntity ae = statisticsDao.getStatistic("Unit");
        assertEquals(statisticsEntity.getUnit(), ae.getUnit());
    }

    @Test
    public void deleteAppointmentTest() throws Exception {
        StatisticsEntity statisticsEntity = new StatisticsEntity("Unit", null);
        statisticsDao.insertAll(statisticsEntity);

        // Ensure the database contains the statistic to be deleted
        assertEquals(statisticsEntity.getUnit(), statisticsDao.getStatistic("Unit").getUnit());

        // Delete the statistic from the database and ensure the getUnit query returns null
        statisticsDao.delete(statisticsDao.getStatistic("Unit"));
        assertEquals(null, statisticsDao.getStatistic("Unit"));
    }

    @Test
    public void insertMultipleStatisticTest() throws Exception {
        List<String> titles = Arrays.asList("Unit 1", "Unit 2", "Unit 3", "Unit 4");

        List<StatisticsEntity> statistics = new ArrayList<>();
        for (int i = 1; i < 5; i++)
            statistics.add(new StatisticsEntity("Unit " + i, null));
        statisticsDao.insertAll(statistics.toArray(new StatisticsEntity[statistics.size()]));

        assertEquals(titles, statisticsDao.getAllUnits());
    }

    @Test
    public void deleteAllStatisticTest() throws Exception {
        for (int i = 1; i < 5; i++)
            statisticsDao.insertAll(new StatisticsEntity("Unit " + i, null));

        // Ensure there are currently 4 statistics in the database
        assertEquals(4, statisticsDao.getAll().size());

        // Delete all statistics and ensure there are 0 left
        statisticsDao.deleteAll();
        assertEquals(0, statisticsDao.getAll().size());
    }
}
