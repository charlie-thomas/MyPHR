package com.csbgroup.myphr;

import android.support.v4.app.FragmentTransaction;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class ContactTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mainActivity;

    private ListView contactsList;

    public ContactTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        mainActivity = getActivity();

        Contacts contacts = Contacts.newInstance();

        FragmentTransaction transaction = mainActivity.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, contacts);
        transaction.commitAllowingStateLoss();

        getInstrumentation().waitForIdleSync();

        contactsList = getActivity().findViewById(R.id.contacts_list);
    }

    public void testPreconditions() {
        assertNotNull(mainActivity);
        assertNotNull(contactsList);
    }

    public void testContactList() {
        assertEquals(contactsList.getChildCount(), 2);
    }

    public void testFabOnClick() {
        onView(withId(R.id.contact_fab)).perform(click());
        onView(withText("Add a New Contact")).check(matches(isDisplayed()));
    }

    public void testErrorDialog() {
        onView(withId(R.id.contact_fab)).perform(click());
        onView(withText("Add a New Contact")).check(matches(isDisplayed()));

        onView(withText("ADD")).perform(click());
        onView(withText("Format Error")).check(matches(isDisplayed()));
    }

    public void testCorrectFormat() {
        onView(withId(R.id.contact_fab)).perform(click());
        onView(withText("Add a New Contact")).check(matches(isDisplayed()));

        onView(withId(R.id.contact_name)).perform(typeText("Contact Name"));
        onView(withId(R.id.contact_phone)).perform(typeText("Contact Phone"));
        onView(withId(R.id.contact_email)).perform(typeText("Contact Email"));
        onView(withId(R.id.contact_notes)).perform(typeText("Contact Notes"));


        onView(withText("ADD")).perform(click());
        onView(withText("Contact Name")).check(matches(isDisplayed()));
    }
}
