package com.csbgroup.myphr;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.test.ActivityInstrumentationTestCase2;

import com.csbgroup.myphr.Calendar.CalendarDay;
import com.csbgroup.myphr.Calendar.CalendarMonth;
import com.csbgroup.myphr.database.AppDatabase;
import com.csbgroup.myphr.database.AppointmentsDao;
import com.csbgroup.myphr.database.AppointmentsEntity;
import com.csbgroup.myphr.database.MedicineDao;
import com.csbgroup.myphr.database.MedicineEntity;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static android.support.test.espresso.Espresso.*;

public class CalendarTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mainActivity;
    private MaterialCalendarView cv;

    public CalendarTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        mainActivity = getActivity();
        populateAppointments();
        populateMedicine();

        CalendarMonth calendarMonth = CalendarMonth.newInstance();

        FragmentTransaction transaction = mainActivity.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, calendarMonth);
        transaction.commitAllowingStateLoss();

        getInstrumentation().waitForIdleSync();

        cv = mainActivity.findViewById(R.id.calendarView);
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


    public void testPreconditions() {
        assertNotNull(mainActivity);
        assertNotNull(cv);
    }

    public void testSelectCalendarDate() {
        String d = getDateString(Calendar.getInstance().getTime());

        CalendarDay cd = CalendarDay.newInstance();
        Bundle bundle = new Bundle();
        bundle.putString("date", d);
        cd.setArguments(bundle);
        getActivity().switchFragment(cd, false);

        onView(withText(d)).check(matches(isDisplayed()));
    }

    public void testUpcomingAppointment() {
        onView(withId(R.id.upcoming_layout)).perform(scrollTo(), click());
        onView(withText("15:55")).check(matches(isDisplayed()));
    }

    public void testTodaysMedicine() {
        onView(withText("Oestrogen")).check(matches(isDisplayed()));
        onView(withText("Oestrogen")).perform(click());
        onView(withText("15:50")).check(matches(isDisplayed()));
    }

    public void testSwitchingDates() {
        Calendar c = Calendar.getInstance();
        String d = getDateString(c.getTime());

        CalendarDay cd = CalendarDay.newInstance();
        Bundle bundle = new Bundle();
        bundle.putString("date", d);
        cd.setArguments(bundle);
        getActivity().switchFragment(cd, false);

        onView(withId(R.id.previous_date)).perform(click());
        c.add(Calendar.DATE, -1);
        onView(withText(getDateString(c.getTime()))).check(matches(isDisplayed()));

        onView(withId(R.id.next_date)).perform(click());
        onView(withId(R.id.next_date)).perform(click());
        c.add(Calendar.DATE, 2);
        onView(withText(getDateString(c.getTime()))).check(matches(isDisplayed()));
    }

    public void testBackButton() {
        Calendar c = Calendar.getInstance();
        String d = getDateString(c.getTime());

        CalendarDay cd = CalendarDay.newInstance();
        Bundle bundle = new Bundle();
        bundle.putString("date", d);
        cd.setArguments(bundle);
        getActivity().switchFragment(cd, false);

        onView(withContentDescription("Navigate up")).perform(click());
        onView(withText("Clinic 1")).check(matches(isDisplayed()));
    }

    private String getDateString(Date d) {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        return df.format(d);
    }
}
