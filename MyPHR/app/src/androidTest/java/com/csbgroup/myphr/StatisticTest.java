package com.csbgroup.myphr;

import android.support.v4.app.FragmentTransaction;
import android.test.ActivityInstrumentationTestCase2;

import com.csbgroup.myphr.Statistics.Statistics;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class StatisticTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mainActivity;

    public StatisticTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        mainActivity = getActivity();

        Statistics statistics  = Statistics.newInstance();

        FragmentTransaction transaction = mainActivity.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, statistics);
        transaction.commitAllowingStateLoss();

        getInstrumentation().waitForIdleSync();
    }

    public void testPreconditions() {
        assertNotNull(mainActivity);
        onView(withText("My Measurements")).check(matches(isDisplayed()));
    }

    public void testMeasurementsList() {
        onView(withText("Blood Pressure")).perform(click());
        onView(withId(R.id.statistics_graph_list)).check(matches(isDisplayed()));
    }

    public void testFormatError() {
        onView(withText("Height")).perform(click());

        onView(withId(R.id.s_fab)).perform(click());
        onView(withText("Add a New Height")).check(matches(isDisplayed()));

        onView(withId(R.id.meas_DD)).perform(typeText("2"));
        onView(withId(R.id.date_error)).check(matches(isDisplayed()));
    }

    public void testAddNewStatistic() {
        onView(withText("Weight")).perform(click());

        onView(withId(R.id.s_fab)).perform(click());
        onView(withText("Add a New Weight")).check(matches(isDisplayed()));

        onView(withId(R.id.measurement)).perform(typeText("50"));
        onView(withId(R.id.meas_DD)).perform(typeText("02"));
        onView(withId(R.id.meas_MM)).perform(typeText("02"));
        onView(withId(R.id.meas_YYYY)).perform(typeText("2018"));
        onView(withId(R.id.centile)).perform(typeText("50"));

        onView(withText("ADD")).perform(click());
        onView(withId(R.id.statistics_graph_list)).check(matches(isDisplayed()));
    }
}