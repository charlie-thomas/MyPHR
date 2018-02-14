package com.csbgroup.myphr;

import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;

import com.csbgroup.myphr.database.AppDatabase;
import com.csbgroup.myphr.database.AppointmentsDao;

import org.junit.Test;

public class AppointmentTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mainActivity;
    private ListView appointmentsList;
    private FloatingActionButton fab;

    public AppointmentTest() {
        super(MainActivity.class);
    }

//    @Override
//    protected void setUp() throws Exception {
//        super.setUp();
//
//        mainActivity = getActivity();
//
//        getInstrumentation().waitForIdleSync();
//    }
//
//    public void testPreconditions() {
//        assertNotNull(mainActivity);
//        assertNotNull(appointmentsList);
//        assertNotNull(fab);
//    }
//
//    public void testAppointmentsList() {
//        AppointmentsDao dao = AppDatabase.getAppDatabase(mainActivity).appointmentsDao();
//
//        assertEquals(appointmentsList.getChildCount(), dao.getAll().size());
//    }
}
