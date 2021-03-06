package com.csbgroup.myphr;

import android.support.test.espresso.ViewInteraction;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TabHost;
import android.widget.TabWidget;

import com.csbgroup.myphr.database.AppDatabase;
import com.csbgroup.myphr.database.AppointmentsDao;
import com.csbgroup.myphr.database.AppointmentsEntity;
import com.csbgroup.myphr.database.InvestigationsDao;
import com.csbgroup.myphr.database.InvestigationsEntity;
import com.csbgroup.myphr.database.MedicineDao;
import com.csbgroup.myphr.database.MedicineEntity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;

import java.text.SimpleDateFormat;
import java.util.Calendar;

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
        populateAppointments();
        populateMedicine();
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
        onView(withText("My Medication")).check(matches(isDisplayed()));
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
        onView(withText("Height")).perform(click());

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

    private void populateAppointments() {
        AppointmentsDao dao = AppDatabase.getAppDatabase(getInstrumentation().getContext()).appointmentsDao();
        dao.deleteAll();

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

        c.add(Calendar.DATE, 1);
        dao.insertAll(new AppointmentsEntity("Clinic 1", "Royal Hospital for Children, Glasgow",
                df.format(c.getTime()), "15:55","Go to desk on ground floor ward 2.", true, 1,
                false, false, false));
        c.add(Calendar.DATE, 1);
        dao.insertAll(new AppointmentsEntity("Check Up 1", "Children's Hospital",
                df.format(c.getTime()), "16:00","Appointment Notes", false, 0,
                false, false, false));
        c.add(Calendar.DATE, 1);
        dao.insertAll(new AppointmentsEntity("Check Up 2", "Children's Hospital",
                df.format(c.getTime()), "16:00","Appointment Notes", true, 0,
                false, false, false));
        c.add(Calendar.DATE, 1);
        dao.insertAll(new AppointmentsEntity("Clinic 2", "Children's Hospital",
                df.format(c.getTime()), "17:00","Appointment Notes", false, 1,
                false, false, false));
        c.add(Calendar.DATE, 1);
        dao.insertAll(new AppointmentsEntity("Check Up 3", "Children's Hospital",
                df.format(c.getTime()), "14:00","Appointment Notes", true, 1,
                false, false, false));

        InvestigationsDao inv_dao = AppDatabase.getAppDatabase(getInstrumentation().getContext()).investigationDao();
        inv_dao.deleteAll();

        InvestigationsEntity ie = new InvestigationsEntity("Blood Test", "03/01/2018", "Due again in 6 months time (03/07/2018)");
        InvestigationsEntity ie1 = new InvestigationsEntity("Hearing Test", "29/12/2017", "Due again in 12 months (29/12/2018)");
        InvestigationsEntity ie2 = new InvestigationsEntity("Blood Test", "04/06/2017", "Due again in 6 months (04/12/2017)");
        InvestigationsEntity ie3 = new InvestigationsEntity("Hearing Test", "30/06/2017", "Due again in 12 months (30/06/2018)");

        inv_dao.insertAll(ie, ie1, ie2, ie3);
    }

    private void populateMedicine() {
        MedicineDao dao = AppDatabase.getAppDatabase(getInstrumentation().getContext()).medicineDao();
        dao.deleteAll();

        dao.insertAll(
                new MedicineEntity(
                        "Oestrogen",
                        "Helps in the development and maintenance of sexual maturation.",
                        "2mg",
                        "Tablets/patches should be taken once a day, every day.",
                        true,
                        1,
                        true,
                        false,
                        "26/02/2018",
                        "15:50"),
                new MedicineEntity(
                        "Progesterone",
                        "Sex hormone involved in the menstrual cycle, pregnancy and embryogenesis",
                        "5mg",
                        "To be taken on 7-12 days of calendar month either monthly, every 2nd month or" +
                                "every 3rd month.",
                        false,
                        0,
                        true,
                        true,
                        "05/02/2018",
                        "13:30"),
                new MedicineEntity(
                        "Thyroxine",
                        "Main thyroid hormone",
                        "2mg",
                        "Vital roles in regulating the body’s metabolic rate, heart and digestive " +
                                "functions, muscle control, brain development and maintenance of bones.\nTo be taken daily.",
                        true,
                        1,
                        false,
                        true,
                        "01/01/2010",
                        "13:15"),
                new MedicineEntity(
                        "Growth Hormone",
                        "Natural hormone to simulate growth.",
                        "5mg",
                        "To be taken once a day, every day.",
                        false,
                        1,
                        true,
                        false,
                        "26/02/2018",
                        "21:15")
        );
    }
}