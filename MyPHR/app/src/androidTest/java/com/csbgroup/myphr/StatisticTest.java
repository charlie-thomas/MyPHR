package com.csbgroup.myphr;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.test.InstrumentationRegistry;
import android.support.v4.app.FragmentTransaction;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;

import com.csbgroup.myphr.database.AppDatabase;
import com.csbgroup.myphr.database.MedicineDao;

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

    public void testSettings() {
        onView(withContentDescription("Settings Cog")).perform(click());
        onView(withText("Weight Metric")).check(matches(isDisplayed()));
    }

    public void testMeasurementsList() {
        onView(withText("Blood Pressure")).perform(click());
        onView(withId(R.id.statistics_graph_list)).check(matches(isDisplayed()));
    }


}