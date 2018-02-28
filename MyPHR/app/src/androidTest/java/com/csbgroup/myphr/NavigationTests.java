package com.csbgroup.myphr;

import android.support.test.espresso.ViewInteraction;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TabHost;
import android.widget.TabWidget;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class NavigationTests extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mainActivity;

    public NavigationTests() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        mainActivity = getActivity();
    }

    /* Navigation Bar */
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

    /* Tab switching */
    public void testMedicineTab() {
        onView(withId(R.id.medicine)).perform(click());
        onView(withText("Growth Hormone")).check(matches(isDisplayed()));

        ViewInteraction tab1 = onView(allOf(
                childAtPosition(allOf(withId(android.R.id.tabs),
                        childAtPosition(withClassName(is("android.widget.LinearLayout")), 0)), 1), isDisplayed()));
        tab1.perform(click());
        onView(withText(R.string.hospital_address)).check(matches(isDisplayed()));

        ViewInteraction tab0 = onView(allOf(
                childAtPosition(allOf(withId(android.R.id.tabs),
                        childAtPosition(withClassName(is("android.widget.LinearLayout")), 0)), 0), isDisplayed()));
        tab0.perform(click());
        onView(withText("Growth Hormone")).check(matches(isDisplayed()));
    }

    public void testAppointmentsTab() {
        onView(withId(R.id.appointments)).perform(click());
        onView(withText("Check Up 3")).check(matches(isDisplayed()));

        ViewInteraction tab1 = onView(allOf(
                childAtPosition(allOf(withId(android.R.id.tabs),
                        childAtPosition(withClassName(is("android.widget.LinearLayout")), 0)), 1), isDisplayed()));
        tab1.perform(click());
        onView(withText("03/01/2018")).check(matches(isDisplayed()));

        ViewInteraction tab0 = onView(allOf(
                childAtPosition(allOf(withId(android.R.id.tabs),
                        childAtPosition(withClassName(is("android.widget.LinearLayout")), 0)), 0), isDisplayed()));
        tab0.perform(click());
        onView(withText("Check Up 3")).check(matches(isDisplayed()));
    }

    public void testStatisticsTab() {
        onView(withId(R.id.statistics)).perform(click());
        onView(withText("Weight")).perform(click());

        ViewInteraction tab1 = onView(allOf(
                childAtPosition(allOf(withId(android.R.id.tabs),
                        childAtPosition(withClassName(is("android.widget.LinearLayout")), 0)), 1), isDisplayed()));
        tab1.perform(click());

        ViewInteraction tab0 = onView(allOf(
                childAtPosition(allOf(withId(android.R.id.tabs),
                        childAtPosition(withClassName(is("android.widget.LinearLayout")), 0)), 0), isDisplayed()));
        tab0.perform(click());
        onView(withId(R.id.statistics_graph_list)).check(matches(isDisplayed()));
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}