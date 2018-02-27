package com.csbgroup.myphr;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.test.InstrumentationRegistry;
import android.support.v4.app.FragmentTransaction;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;

import com.csbgroup.myphr.database.AppDatabase;
import com.csbgroup.myphr.database.AppointmentsDao;

public class AppointmentTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mainActivity;

    private ListView appointmentsList;
    private FloatingActionButton fab;

    public AppointmentTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        mainActivity = getActivity();

        Appointments appointmentsFrag = Appointments.newInstance();

        FragmentTransaction transaction = mainActivity.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, appointmentsFrag);
        transaction.commitAllowingStateLoss();

        getInstrumentation().waitForIdleSync();

        appointmentsList = getActivity().findViewById(R.id.appointments_list);
        fab = getActivity().findViewById(R.id.app_fab);
    }

    public void testPreconditions() {
        assertNotNull(mainActivity);
        assertNotNull(appointmentsList);
        assertNotNull(fab);
    }

    public void testAppointmentsList() {
        Context context = InstrumentationRegistry.getTargetContext();
        AppDatabase appDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        AppointmentsDao dao = appDatabase.appointmentsDao();

        assertEquals(appointmentsList.getChildCount(), dao.getAll().size());
    }
}
