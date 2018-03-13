package com.csbgroup.myphr.Espresso;


import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.csbgroup.myphr.Login.LoginActivity;
import com.csbgroup.myphr.Login.StartupActivity;
import com.csbgroup.myphr.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class GeneralUITest {

    @Rule
    public ActivityTestRule<StartupActivity> mActivityTestRule = new ActivityTestRule<>(StartupActivity.class);

    @Test
    public void generalUITest() {
        ViewInteraction imageView = onView(
                allOf(withId(R.id.iconLoginStart), withContentDescription("App Icon"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        imageView.check(matches(isDisplayed()));

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.button), withText("5"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.initial_pin),
                                        4),
                                0),
                        isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.button), withText("5"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.initial_pin),
                                        4),
                                0),
                        isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.button), withText("5"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.initial_pin),
                                        4),
                                0),
                        isDisplayed()));
        appCompatButton3.perform(click());

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.button), withText("5"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.initial_pin),
                                        4),
                                0),
                        isDisplayed()));
        appCompatButton4.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.security_1_answer),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        appCompatEditText.perform(click());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.security_1_answer),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("a"), closeSoftKeyboard());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.security_1_answer), withText("a"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        appCompatEditText3.perform(pressImeActionButton());

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.security_2_answer),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                5),
                        isDisplayed()));
        appCompatEditText4.perform(replaceText("a"), closeSoftKeyboard());

        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.security_2_answer), withText("a"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                5),
                        isDisplayed()));
        appCompatEditText5.perform(pressImeActionButton());

        ViewInteraction appCompatButton5 = onView(
                allOf(withId(R.id.saveQuestions), withText("Save"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                6),
                        isDisplayed()));
        appCompatButton5.perform(click());

        ViewInteraction imageView2 = onView(
                allOf(withId(R.id.iconLogin), withContentDescription("App Icon"),
                        childAtPosition(
                                allOf(withId(R.id.login_layout),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                0),
                        isDisplayed()));
        imageView2.check(matches(isDisplayed()));

        ViewInteraction appCompatButton6 = onView(
                allOf(withId(R.id.button), withText("5"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.pin_lock_view),
                                        4),
                                0),
                        isDisplayed()));
        appCompatButton6.perform(click());

        ViewInteraction appCompatButton7 = onView(
                allOf(withId(R.id.button), withText("5"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.pin_lock_view),
                                        4),
                                0),
                        isDisplayed()));
        appCompatButton7.perform(click());

        ViewInteraction appCompatButton8 = onView(
                allOf(withId(R.id.button), withText("5"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.pin_lock_view),
                                        4),
                                0),
                        isDisplayed()));
        appCompatButton8.perform(click());

        ViewInteraction appCompatButton9 = onView(
                allOf(withId(R.id.button), withText("5"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.pin_lock_view),
                                        4),
                                0),
                        isDisplayed()));
        appCompatButton9.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(16);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.appointments),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom_nav),
                                        0),
                                3),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.app_fab),
                        childAtPosition(
                                allOf(withId(R.id.appointments_layout),
                                        childAtPosition(
                                                withId(R.id.appointments_tab),
                                                0)),
                                2),
                        isDisplayed()));
        floatingActionButton.perform(click());

        ViewInteraction appCompatEditText6 = onView(
                allOf(withId(R.id.appointment_name),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                2),
                        isDisplayed()));
        appCompatEditText6.perform(click());

        ViewInteraction appCompatEditText7 = onView(
                allOf(withId(R.id.appointment_name),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                2),
                        isDisplayed()));
        appCompatEditText7.perform(replaceText("Ti"), closeSoftKeyboard());

        ViewInteraction appCompatEditText8 = onView(
                allOf(withId(R.id.appointment_location),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                3),
                        isDisplayed()));
        appCompatEditText8.perform(replaceText("Name"), closeSoftKeyboard());

        ViewInteraction appCompatEditText9 = onView(
                allOf(withId(R.id.appointment_DD),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout1),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                1)),
                                0),
                        isDisplayed()));
        appCompatEditText9.perform(replaceText("6"), closeSoftKeyboard());

        ViewInteraction editText = onView(
                allOf(withId(R.id.date_error),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout1),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                                1)),
                                3),
                        isDisplayed()));
        editText.check(matches(withText("")));

        ViewInteraction appCompatEditText10 = onView(
                allOf(withId(R.id.appointment_hour),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout1),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                1)),
                                4),
                        isDisplayed()));
        appCompatEditText10.perform(replaceText("6"), closeSoftKeyboard());

        ViewInteraction editText2 = onView(
                allOf(withId(R.id.time_error),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout1),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                                1)),
                                6),
                        isDisplayed()));
        editText2.check(matches(withText("")));

        ViewInteraction appCompatButton10 = onView(
                allOf(withId(android.R.id.button2), withText("Cancel"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                2)));
        appCompatButton10.perform(scrollTo(), click());

        ViewInteraction bottomNavigationItemView2 = onView(
                allOf(withId(R.id.medicine),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom_nav),
                                        0),
                                1),
                        isDisplayed()));
        bottomNavigationItemView2.perform(click());

        ViewInteraction floatingActionButton2 = onView(
                allOf(withId(R.id.med_fab),
                        childAtPosition(
                                allOf(withId(R.id.medicine_layout),
                                        childAtPosition(
                                                withId(R.id.medicine_tab),
                                                0)),
                                2),
                        isDisplayed()));
        floatingActionButton2.perform(click());

        ViewInteraction appCompatButton11 = onView(
                allOf(withId(android.R.id.button2), withText("CANCEL"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                2)));
        appCompatButton11.perform(scrollTo(), click());

        ViewInteraction bottomNavigationItemView3 = onView(
                allOf(withId(R.id.statistics),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom_nav),
                                        0),
                                4),
                        isDisplayed()));
        bottomNavigationItemView3.perform(click());

        DataInteraction appCompatTextView = onData(anything())
                .inAdapterView(allOf(withId(R.id.statistics_list),
                        childAtPosition(
                                withId(R.id.statistics_layout),
                                0)))
                .atPosition(5);
        appCompatTextView.perform(click());

        ViewInteraction appCompatImageButton = onView(
                allOf(withContentDescription("Navigate up"),
                        childAtPosition(
                                allOf(withId(R.id.toolbar),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        DataInteraction appCompatTextView2 = onData(anything())
                .inAdapterView(allOf(withId(R.id.statistics_list),
                        childAtPosition(
                                withId(R.id.statistics_layout),
                                0)))
                .atPosition(3);
        appCompatTextView2.perform(click());

        ViewInteraction floatingActionButton3 = onView(
                allOf(withId(R.id.s_fab),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                2)));
        floatingActionButton3.perform(scrollTo(), click());

        ViewInteraction appCompatEditText11 = onView(
                allOf(withId(R.id.measurement),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                2),
                        isDisplayed()));
        appCompatEditText11.perform(click());

        ViewInteraction appCompatEditText12 = onView(
                allOf(withId(R.id.measurement),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                2),
                        isDisplayed()));
        appCompatEditText12.perform(replaceText("2"), closeSoftKeyboard());

        ViewInteraction appCompatEditText13 = onView(
                allOf(withId(R.id.meas_DD),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout1),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                1)),
                                0),
                        isDisplayed()));
        appCompatEditText13.perform(replaceText("0"), closeSoftKeyboard());

        ViewInteraction editText3 = onView(
                allOf(withId(R.id.date_error),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout1),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                                1)),
                                3),
                        isDisplayed()));
        editText3.check(matches(withText("")));

        ViewInteraction appCompatEditText14 = onView(
                allOf(withId(R.id.meas_DD), withText("0"),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout1),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                1)),
                                0),
                        isDisplayed()));
        appCompatEditText14.perform(replaceText("03"));

        ViewInteraction appCompatEditText15 = onView(
                allOf(withId(R.id.meas_DD), withText("03"),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout1),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                1)),
                                0),
                        isDisplayed()));
        appCompatEditText15.perform(closeSoftKeyboard());

        ViewInteraction appCompatEditText16 = onView(
                allOf(withId(R.id.meas_MM),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout1),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                1)),
                                1),
                        isDisplayed()));
        appCompatEditText16.perform(replaceText("03"), closeSoftKeyboard());

        ViewInteraction appCompatEditText17 = onView(
                allOf(withId(R.id.meas_YYYY),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout1),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                1)),
                                2),
                        isDisplayed()));
        appCompatEditText17.perform(replaceText("2003"), closeSoftKeyboard());

        ViewInteraction appCompatEditText18 = onView(
                allOf(withId(R.id.centile),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        2),
                                0),
                        isDisplayed()));
        appCompatEditText18.perform(replaceText("50"), closeSoftKeyboard());

        ViewInteraction appCompatButton12 = onView(
                allOf(withId(android.R.id.button1), withText("ADD"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        appCompatButton12.perform(scrollTo(), click());

        ViewInteraction bottomNavigationItemView4 = onView(
                allOf(withId(R.id.statistics),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom_nav),
                                        0),
                                4),
                        isDisplayed()));
        bottomNavigationItemView4.perform(click());

        DataInteraction appCompatTextView3 = onData(anything())
                .inAdapterView(allOf(withId(R.id.statistics_list),
                        childAtPosition(
                                withId(R.id.statistics_layout),
                                0)))
                .atPosition(4);
        appCompatTextView3.perform(click());

        ViewInteraction bottomNavigationItemView5 = onView(
                allOf(withId(R.id.statistics),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom_nav),
                                        0),
                                4),
                        isDisplayed()));
        bottomNavigationItemView5.perform(click());

        DataInteraction appCompatTextView4 = onData(anything())
                .inAdapterView(allOf(withId(R.id.statistics_list),
                        childAtPosition(
                                withId(R.id.statistics_layout),
                                0)))
                .atPosition(3);
        appCompatTextView4.perform(click());

        ViewInteraction floatingActionButton4 = onView(
                allOf(withId(R.id.s_fab),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                2)));
        floatingActionButton4.perform(scrollTo(), click());

        ViewInteraction appCompatEditText19 = onView(
                allOf(withId(R.id.measurement),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                2),
                        isDisplayed()));
        appCompatEditText19.perform(click());

        ViewInteraction appCompatEditText20 = onView(
                allOf(withId(R.id.measurement),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                2),
                        isDisplayed()));
        appCompatEditText20.perform(replaceText("3"), closeSoftKeyboard());

        ViewInteraction appCompatEditText21 = onView(
                allOf(withId(R.id.meas_DD),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout1),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                1)),
                                0),
                        isDisplayed()));
        appCompatEditText21.perform(replaceText("03"), closeSoftKeyboard());

        ViewInteraction appCompatEditText22 = onView(
                allOf(withId(R.id.meas_MM),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout1),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                1)),
                                1),
                        isDisplayed()));
        appCompatEditText22.perform(click());

        ViewInteraction appCompatEditText23 = onView(
                allOf(withId(R.id.meas_MM),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout1),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                1)),
                                1),
                        isDisplayed()));
        appCompatEditText23.perform(replaceText("03"), closeSoftKeyboard());

        ViewInteraction appCompatEditText24 = onView(
                allOf(withId(R.id.meas_YYYY),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout1),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                1)),
                                2),
                        isDisplayed()));
        appCompatEditText24.perform(replaceText("2004"), closeSoftKeyboard());

        ViewInteraction appCompatEditText25 = onView(
                allOf(withId(R.id.centile),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        2),
                                0),
                        isDisplayed()));
        appCompatEditText25.perform(replaceText("50"), closeSoftKeyboard());

        ViewInteraction appCompatButton13 = onView(
                allOf(withId(android.R.id.button1), withText("ADD"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        appCompatButton13.perform(scrollTo(), click());

        ViewInteraction bottomNavigationItemView6 = onView(
                allOf(withId(R.id.statistics),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom_nav),
                                        0),
                                4),
                        isDisplayed()));
        bottomNavigationItemView6.perform(click());

        DataInteraction appCompatTextView5 = onData(anything())
                .inAdapterView(allOf(withId(R.id.statistics_list),
                        childAtPosition(
                                withId(R.id.statistics_layout),
                                0)))
                .atPosition(4);
        appCompatTextView5.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.textView2), withText("1.0cm/year"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.statistics_graph_list),
                                        0),
                                1),
                        isDisplayed()));
        textView.check(matches(withText("1.0cm/year")));

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
