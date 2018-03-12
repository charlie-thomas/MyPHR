package com.csbgroup.myphr;

import android.support.v4.app.FragmentTransaction;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;

import com.csbgroup.myphr.Appointments.Appointments;

import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
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
        onView(withText("Add an Appointment")).check(matches(isDisplayed()));
    }

    public void testErrorDialog() {
        onView(withId(R.id.app_fab)).perform(click());
        onView(withText("Add an Appointment")).check(matches(isDisplayed()));

        onView(withId(R.id.appointment_DD)).perform(typeText("3"));
        onView(withId(R.id.date_error)).check(matches(isDisplayed()));

        onView(withId(R.id.appointment_MM)).perform(typeText("3"));
        onView(withId(R.id.time_error)).check(matches(isDisplayed()));
    }

    public void testCorrectFormat() {
        onView(withId(R.id.app_fab)).perform(click());
        onView(withText("Add an Appointment")).check(matches(isDisplayed()));

        onView(withId(R.id.appointment_name)).perform(typeText("App Name"));
        onView(withId(R.id.appointment_location)).perform(typeText("App"));
        onView(withId(R.id.appointment_DD)).perform(typeText("03"));
        onView(withId(R.id.appointment_MM)).perform(typeText("12"));
        onView(withId(R.id.appointment_YYYY)).perform(typeText("1997"));
        onView(withId(R.id.appointment_hour)).perform(typeText("12"));
        onView(withId(R.id.appointment_min)).perform(typeText("12"));
        onView(withId(R.id.appointment_notes)).perform(typeText("App"));

        onView(withText("ADD")).perform(click());
        onView(withText("App Name")).check(matches(isDisplayed()));
    }

    public void testBackButton() {
        onView(withText("Check Up 3")).perform(click());

        onView(withContentDescription("Navigate up")).perform(click());
        onView(withText("Clinic 2")).check(matches(isDisplayed()));
    }

    public void testEditButton() {
        onView(withText("Check Up 3")).perform(click());

        onView(withContentDescription("Edit Icon")).perform(click());
        onView(withId(R.id.appointments_title)).check(matches(isDisplayed()));

        onView(withId(R.id.appointments_title)).perform(clearText());
        onView(withId(R.id.appointments_title)).perform(typeText("New Title"));
        onView(withContentDescription("Edit Icon")).perform(click());
        onView(withText("New Title")).check(matches(isDisplayed()));
    }
}
