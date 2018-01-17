package com.csbgroup.myphr;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.csbgroup.myphr.database.AppDatabase;
import com.csbgroup.myphr.database.StatisticsDao;

import org.junit.After;
import org.junit.Before;

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
}
