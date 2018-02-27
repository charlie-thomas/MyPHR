package com.csbgroup.myphr;

import android.support.v4.app.FragmentTransaction;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static android.support.test.espresso.Espresso.*;

public class AppointmentTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mainActivity;

    private ListView appointmentsList;

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
    }

    public void testPreconditions() {
        assertNotNull(mainActivity);
        assertNotNull(appointmentsList);
    }

    public void testAppointmentsList() {
        assertEquals(appointmentsList.getChildCount(), 5);
    }

    public void testFabOnClick() {
        onView(withId(R.id.app_fab)).perform(click());
        onView(withText("Add a New Appointment")).check(matches(isDisplayed()));
    }
}
