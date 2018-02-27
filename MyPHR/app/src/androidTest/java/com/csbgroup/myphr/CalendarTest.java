package com.csbgroup.myphr;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.CalendarView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static android.support.test.espresso.Espresso.*;

public class CalendarTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mainActivity;
    private CalendarView cv;

    public CalendarTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        mainActivity = getActivity();

        CalendarMonth calendarMonth = CalendarMonth.newInstance();

        FragmentTransaction transaction = mainActivity.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, calendarMonth);
        transaction.commitAllowingStateLoss();

        getInstrumentation().waitForIdleSync();

        cv = mainActivity.findViewById(R.id.calendarView);
    }

    public void testPreconditions() {
        assertNotNull(mainActivity);
        assertNotNull(cv);
    }

    public void testSelectCalendarDate() {
        Date d = Calendar.getInstance().getTime();
        cv.setDate(d.getTime());

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

        CalendarDay cd = CalendarDay.newInstance();
        Bundle bundle = new Bundle();
        bundle.putString("date", df.format(d));
        cd.setArguments(bundle);
        getActivity().switchFragment(cd);

        onView(withText(df.format(d))).check(matches(isDisplayed()));
    }

    public void testUpcomingAppointment() {
        onView(withText("Clinic 1")).check(matches(isDisplayed()));

        onView(withId(R.id.upcoming_layout)).perform(click());
        onView(withText("15:55")).check(matches(isDisplayed()));
    }

    public void testTodaysMedicine() {
        onView(withText("Oestrogen")).check(matches(isDisplayed()));
    }
}
