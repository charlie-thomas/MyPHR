package com.csbgroup.myphr;

import android.support.v4.app.FragmentTransaction;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;

import com.csbgroup.myphr.Medicine.Medicine;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class MedicineTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mainActivity;

    private ListView medicineList;

    public MedicineTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        mainActivity = getActivity();

        Medicine medicine  = Medicine.newInstance();

        FragmentTransaction transaction = mainActivity.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, medicine);
        transaction.commitAllowingStateLoss();

        getInstrumentation().waitForIdleSync();

        medicineList = getActivity().findViewById(R.id.medicine_list);
    }

    public void testPreconditions() {
        assertNotNull(mainActivity);
        assertNotNull(medicineList);
    }

    public void testMedicineList() {
        assertEquals(medicineList.getChildCount(), 4);
    }

    public void testFabOnClick() {
        onView(withId(R.id.med_fab)).perform(click());
        onView(withText("Add a Medication")).check(matches(isDisplayed()));
    }

    public void testCorrectFormat() {
        onView(withId(R.id.med_fab)).perform(click());
        onView(withText("Add a Medication")).check(matches(isDisplayed()));

        onView(withId(R.id.med_name)).perform(typeText("Med Name"));
        onView(withId(R.id.med_description)).perform(typeText("Med"));
        onView(withId(R.id.med_dose)).perform(typeText("Med"));
        onView(withId(R.id.med_notes)).perform(typeText("Med"));

        onView(withText("ADD")).perform(click());
        onView(withText("Med Name")).check(matches(isDisplayed()));
    }

    public void testBackButton() {
        onView(withText("Growth Hormone")).perform(click());

        onView(withContentDescription("Navigate up")).perform(click());
        onView(withText("Thyroxine")).check(matches(isDisplayed()));
    }

    public void testEditButton() {
        onView(withText("Growth Hormone")).perform(click());

        onView(withContentDescription("Edit Icon")).perform(click());
        onView(withId(R.id.medicine_title)).check(matches(isDisplayed()));

        onView(withId(R.id.medicine_title)).perform(clearText());
        onView(withId(R.id.medicine_title)).perform(typeText("New Title"));
        onView(withContentDescription("Edit Icon")).perform(click());

        onView(withText("New Title")).check(matches(isDisplayed()));
    }

    public void testRemindersSwitch() {
        onView(withText("Growth Hormone")).perform(click());

        onView(withId(R.id.reminder_switch)).perform(click());
        onView(withId(R.id.everyotherday)).check(matches(isDisplayed()));

        onView(withId(R.id.everyotherday)).perform(click());
    }


}