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
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
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
        onView(withText("Add a New Medicine")).check(matches(isDisplayed()));
    }
}