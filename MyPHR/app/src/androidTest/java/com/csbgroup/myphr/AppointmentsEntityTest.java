package com.csbgroup.myphr;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.csbgroup.myphr.database.AppDatabase;
import com.csbgroup.myphr.database.AppointmentsDao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AppointmentsEntityTest {

    private AppointmentsDao appointmentsDao;
    private AppDatabase appDatabase;

    @Before
    public void createDb() {
        Context context = InstrumentationRegistry.getTargetContext();
        appDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        appointmentsDao = appDatabase.appointmentsDao();
    }

    @After
    public void closeDb() {
        appDatabase.close();
    }

    @Test
    public void createAppointmentTest() throws Exception {

    }

    @Test
    public void deleteAppointmentTest() throws Exception {

    }

    @Test
    public void insertMultipleAppointmentsTest() throws Exception {

    }

    @Test
    public void deleteAllAppointmentsTest() throws Exception {

    }
}
