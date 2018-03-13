package com.csbgroup.myphr;

import android.support.v4.app.FragmentTransaction;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;

import com.csbgroup.myphr.Appointments.Investigations;
import com.csbgroup.myphr.database.AppDatabase;
import com.csbgroup.myphr.database.InvestigationsDao;
import com.csbgroup.myphr.database.InvestigationsEntity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;

public class InvestigationTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mainActivity;

    public InvestigationTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        mainActivity = getActivity();
        populateInvestigations();

        Investigations investigations  = Investigations.newInstance();

        FragmentTransaction transaction = mainActivity.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, investigations);
        transaction.commitAllowingStateLoss();

        getInstrumentation().waitForIdleSync();
    }

    private void populateInvestigations()  {
        InvestigationsDao dao = AppDatabase.getAppDatabase(getInstrumentation().getContext()).investigationDao();
        dao.deleteAll();

        InvestigationsEntity ie = new InvestigationsEntity("Blood Test", "03/01/2018", "Due again in 6 months time (03/07/2018)");
        InvestigationsEntity ie1 = new InvestigationsEntity("Hearing Test", "29/12/2017", "Due again in 12 months (29/12/2018)");
        InvestigationsEntity ie2 = new InvestigationsEntity("Blood Test", "04/06/2017", "Due again in 6 months (04/12/2017)");
        InvestigationsEntity ie3 = new InvestigationsEntity("Hearing Test", "30/06/2017", "Due again in 12 months (30/06/2018)");

        dao.insertAll(ie, ie1, ie2, ie3);
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
        onView(withText("Add an Investigation")).check(matches(isDisplayed()));

        onView(withId(R.id.inv_DD)).perform(typeText("2"));
        onView(withId(R.id.date_error)).check(matches(isDisplayed()));
    }

    public void testAddNewInvestigation() {
        onView(withId(R.id.investigation_fab)).perform(click());
        onView(withText("Add an Investigation")).check(matches(isDisplayed()));

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