package com.csbgroup.myphr;

import android.test.ActivityInstrumentationTestCase2;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class NavigationTests extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mainActivity;

    public NavigationTests() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        mainActivity = getActivity();
    }

    public void testContactsNavBar() {
        onView(withId(R.id.contacts)).perform(click());
        onView(withText("My Contacts")).check(matches(isDisplayed()));
    }

    public void testStatsNavBar() {
        onView(withId(R.id.statistics)).perform(click());
        onView(withText("My Measurements")).check(matches(isDisplayed()));
    }

    public void testCalendarNavBar() {
        onView(withId(R.id.calendar)).perform(click());
        onView(withText("My Calendar")).check(matches(isDisplayed()));
    }

    public void testMedicineNavBar() {
        onView(withId(R.id.medicine)).perform(click());
        onView(withText("My Medicine")).check(matches(isDisplayed()));
    }

    public void testAppointmentsNavBar() {
        onView(withId(R.id.appointments)).perform(click());
        onView(withText("My Appointments")).check(matches(isDisplayed()));
    }
}