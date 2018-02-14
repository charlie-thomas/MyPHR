package com.csbgroup.myphr;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.csbgroup.myphr.database.AppDatabase;
import com.csbgroup.myphr.database.AppointmentsDao;
import com.csbgroup.myphr.database.AppointmentsEntity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

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
        AppointmentsEntity appointmentsEntity = new AppointmentsEntity("Appointment",
                "Location", "Date", "Time", "Notes", false);
        appointmentsEntity.setUid(15);
        appointmentsDao.insertAll(appointmentsEntity);

        AppointmentsEntity ae = appointmentsDao.getAppointment(15);
        assertEquals(appointmentsEntity.getTitle(), ae.getTitle());
    }

    @Test
    public void deleteAppointmentTest() throws Exception {
        AppointmentsEntity appointmentsEntity = new AppointmentsEntity("Appointment",
                "Location", "Date", "Time", "Notes", false);
        appointmentsEntity.setUid(16);
        appointmentsDao.insertAll(appointmentsEntity);

        // Ensure the database contains the appointment to be deleted
        assertEquals(appointmentsEntity.getTitle(), appointmentsDao.getAppointment(16).getTitle());

        // Delete the appointment from the database and ensure the getAppointment query returns null
        appointmentsDao.delete(appointmentsDao.getAppointment(16));
        assertEquals(null, appointmentsDao.getAppointment(16));
    }

    @Test
    public void insertMultipleAppointmentsTest() throws Exception {
        List<String> titles = Arrays.asList("App 1", "App 2", "App 3", "App 4");

        List<AppointmentsEntity> appointments = new ArrayList<>();
        for (int i = 1; i < 5; i++)
            appointments.add(new AppointmentsEntity("App " + i, null, null,null, null, false));
        appointmentsDao.insertAll(appointments.toArray(new AppointmentsEntity[appointments.size()]));

        assertEquals(titles, appointmentsDao.getAllTitles());
    }

    @Test
    public void deleteAllAppointmentsTest() throws Exception {
        for (int i = 1; i < 5; i++)
            appointmentsDao.insertAll(new AppointmentsEntity("App " + i, null, null,null, null, false));

        // Ensure there are currently 4 appointments in the database
        assertEquals(4, appointmentsDao.getAll().size());

        // Delete all appointments and ensure there are 0 left
        appointmentsDao.deleteAll();
        assertEquals(0, appointmentsDao.getAll().size());
    }
}
