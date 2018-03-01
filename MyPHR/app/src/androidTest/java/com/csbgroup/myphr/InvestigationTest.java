package com.csbgroup.myphr;

import android.support.v4.app.FragmentTransaction;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class InvestigationTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mainActivity;

    public InvestigationTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        mainActivity = getActivity();

        Investigations investigations  = Investigations.newInstance();

        FragmentTransaction transaction = mainActivity.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, investigations);
        transaction.commitAllowingStateLoss();

        getInstrumentation().waitForIdleSync();
    }

    public void testPreconditions() {
        assertNotNull(mainActivity);
        onView(withId(R.id.investigations_list)).check(matches(isDisplayed()));
    }

    public void testInvestigationsList() {
        ListView lv = mainActivity.findViewById(R.id.investigations_list);
        assertEquals(4, lv.getChildCount());
        onView(withId(R.id.investigations_list)).check(matches(isDisplayed()));
    }

    public void testFormatError() {
        onView(withId(R.id.investigation_fab)).perform(click());
        onView(withText(R.string.investigationmessage)).check(matches(isDisplayed()));

        onView(withText("ADD")).perform(click());
        onView(withText("Format Error")).check(matches(isDisplayed()));
    }

    public void testAddNewInvestigation() {
        onView(withId(R.id.investigation_fab)).perform(click());
        onView(withText(R.string.investigationmessage)).check(matches(isDisplayed()));

        onView(withId(R.id.inv_title)).perform(typeText("Invest Title"));
        onView(withId(R.id.inv_DD)).perform(typeText("02"));
        onView(withId(R.id.inv_MM)).perform(typeText("02"));
        onView(withId(R.id.inv_YYYY)).perform(typeText("2018"));
        onView(withId(R.id.inv_notes)).perform(typeText("Notes"));

        onView(withText("ADD")).perform(click());
        onView(withText("Invest Title")).check(matches(isDisplayed()));
    }

    public void testBackButton() {
        onView(withText("03/01/2018")).perform(click());

        onView(withContentDescription("Navigate up")).perform(click());
        onView(withText("Check Up 3")).check(matches(isDisplayed()));
    }

    public void testEditButton() {
        onView(withText("03/01/2018")).perform(click());

        onView(withContentDescription("Edit Icon")).perform(click());
        onView(withId(R.id.investigation_name)).check(matches(isDisplayed()));

        onView(withId(R.id.investigation_name)).perform(clearText());
        onView(withId(R.id.investigation_name)).perform(typeText("New Title"));
        onView(withContentDescription("Edit Icon")).perform(click());

        onView(withText("New Title")).check(matches(isDisplayed()));
    }
}