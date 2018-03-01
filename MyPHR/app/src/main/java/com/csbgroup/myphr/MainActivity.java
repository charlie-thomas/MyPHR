package com.csbgroup.myphr;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.csbgroup.myphr.database.AppDatabase;
import com.csbgroup.myphr.database.AppointmentsDao;
import com.csbgroup.myphr.database.AppointmentsEntity;
import com.csbgroup.myphr.database.InvestigationsDao;
import com.csbgroup.myphr.database.InvestigationsEntity;
import com.csbgroup.myphr.database.ContactsDao;
import com.csbgroup.myphr.database.ContactsEntity;
import com.csbgroup.myphr.database.MedicineDao;
import com.csbgroup.myphr.database.MedicineEntity;
import com.csbgroup.myphr.database.SickDaysDao;
import com.csbgroup.myphr.database.SickDaysEntity;
import com.csbgroup.myphr.database.StatValueEntity;
import com.csbgroup.myphr.database.StatisticsDao;
import com.csbgroup.myphr.database.StatisticsEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intentAlarm = new Intent(this.getApplicationContext(), AlarmReceiver.class);
        PendingIntent notifySender = PendingIntent.getBroadcast(this, 123, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 3600);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 86400000, notifySender);


        // Populate database with data for debug purposes
        new Thread(new Runnable() {
            @Override
            public void run() {
                AppDatabase db = AppDatabase.getAppDatabase(MainActivity.this);
                populateMedicine(db.medicineDao());
                populateContacts(db.contactsDao());
                populateAppointments(db.appointmentsDao());
                populateStatistics(db.statisticsDao());
                populateInvestigations(db.investigationDao());
                populateSickDays(db.sickDaysDao());
            }
        }).start();

        // Get the bottom nav bar view
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);

        // Switch fragment based on which element on the nav bar is selected
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedPage = null;
                switch (item.getItemId()) {
                    case R.id.contacts:
                        selectedPage = Contacts.newInstance();
                        break;
                    case R.id.medicine:
                        selectedPage = MedicineSection.newInstance();
                        break;
                    case R.id.calendar:
                        selectedPage = CalendarMonth.newInstance();
                        break;
                    case R.id.appointments:
                        selectedPage = AppointmentsSection.newInstance();
                        break;
                    case R.id.statistics:
                        selectedPage = Statistics.newInstance();
                        break;
                }

                switchFragment(selectedPage);
                return true;
            }
        });

        // Show calendar when app first loads
        switchFragment(CalendarMonth.newInstance());
        bottomNavigationView.setSelectedItemId(R.id.calendar);
    }

    /* Helper function to set the title of the toolbar */
    public void setToolbar(String title, boolean back) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(back);
        getSupportActionBar().setDisplayShowHomeEnabled(back);
    }


    /* Helper function to switch the current fragment in the frame */
    public void switchFragment(Fragment newFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, newFragment);
        transaction.commit();
    }

    private static void populateContacts(ContactsDao dao) {
        dao.deleteAll();

        // Key medical staff at Royal Childrens Hospital, Glasgow are pre-loaded
        dao.insertAll(
                new ContactsEntity(
                "Dr Avril Mason",
                "avrilmason@nhs.net",
                "01412010000",
                "Phone will go through to mobile.\n\nGround Floor, Zone 2, Office Block,\nRoyal Hospital for Children,\n" +
                        "QEUH,\nGovan Road,\nGlasgow G51 4TF"),

                new ContactsEntity(
                        "Ms Kerri Marshall",
                        "",
                        "01414516548(ext), 86548(int)",
                        "Endocrine/Metabolic Secretary"),

                new ContactsEntity(
                  "Ms Teresa McBride",
                  "teresa.mcbride@ggc.scot.nhs.uk",
                  "07904881485",
                  "Paediatric Endocrine Nurse"));
    }

    private static void populateMedicine(MedicineDao dao) {
        dao.deleteAll();

        dao.insertAll(
                new MedicineEntity(
                "Oestrogen",
                "Helps in the development and maintenance of sexual maturation.",
                "2mg",
                "Tablets/patches should be taken once a day, every day.",
                true,
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
                true,
                false,
                "26/02/2018",
                "21:15")
                );
    }


    private static void populateAppointments(AppointmentsDao dao)  {
        dao.deleteAll();

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

        c.add(Calendar.DATE, 1);
        dao.insertAll(new AppointmentsEntity("Clinic 1", "Children's Hospital",
                df.format(c.getTime()), "15:55","Appointment Notes", true));
        c.add(Calendar.DATE, 1);
        dao.insertAll(new AppointmentsEntity("Check Up 1", "Children's Hospital",
                df.format(c.getTime()), "16:00","Appointment Notes", true));
        c.add(Calendar.DATE, 1);
        dao.insertAll(new AppointmentsEntity("Check Up 2", "Children's Hospital",
                df.format(c.getTime()), "16:00","Appointment Notes", true));
        c.add(Calendar.DATE, 1);
        dao.insertAll(new AppointmentsEntity("Clinic 2", "Children's Hospital",
                df.format(c.getTime()), "17:00","Appointment Notes", true));
        c.add(Calendar.DATE, 1);
        dao.insertAll(new AppointmentsEntity("Check Up 3", "Children's Hospital",
                df.format(c.getTime()), "14:00","Appointment Notes", true));
    }

    private static void populateInvestigations(InvestigationsDao dao)  {
        dao.deleteAll();

        InvestigationsEntity ie = new InvestigationsEntity("Blood Test", "03/01/2018", "due in 6 months");
        InvestigationsEntity ie1 = new InvestigationsEntity("Hearing Test", "29/12/2017", "due in 12 months");
        InvestigationsEntity ie2 = new InvestigationsEntity("Blood Test", "04/06/2017", "due in 6 months");
        InvestigationsEntity ie3 = new InvestigationsEntity("Hearing Test", "30/06/2017", "due in 12 months");

        dao.insertAll(ie, ie1, ie2, ie3);
    }

    private static void populateSickDays(SickDaysDao dao) {
        dao.deleteAll();

        SickDaysEntity sd = new SickDaysEntity("28/02/2018");
        SickDaysEntity sd1 = new SickDaysEntity("14/03/2018");
        SickDaysEntity sd2 = new SickDaysEntity("01/02/2018");
        SickDaysEntity sd3 = new SickDaysEntity("06/02/2018");

        dao.insertAll(sd, sd1, sd2, sd3);
    }

    private static void populateStatistics(StatisticsDao dao) {
        dao.deleteAll();

        String[] stats = {"Body Mass Index (BMI)", "Head Circumference", "Height",
                "Height Velocity", "Weight"};
        ArrayList<StatValueEntity> list;

        list = new ArrayList<>();
        list.add(new StatValueEntity("60.67/30.43","16/12/2017","54"));
        list.add(new StatValueEntity("69.3/29.43","12/12/2017","47"));
        list.add(new StatValueEntity("52.9/33.43","08/12/2017",null));
        list.add(new StatValueEntity("58/31.45","03/12/2017","51"));
        list.add(new StatValueEntity("52.3/29.78","01/12/2017","55"));
        StatisticsEntity st = new StatisticsEntity("Blood Pressure", list);
        dao.insertAll(st);

        list = new ArrayList<>();

        list.add(new StatValueEntity("50.09","24/01/2018","49"));
        list.add(new StatValueEntity("51.98","20/01/2018","53"));
        list.add(new StatValueEntity("50.67","16/01/2018","54"));
        list.add(new StatValueEntity("49.3","12/01/2018","47"));
        list.add(new StatValueEntity("52.9","08/01/2018",null));
        list.add(new StatValueEntity("52.3","03/01/2018","51"));
        list.add(new StatValueEntity("52.3","01/01/2018","55"));
        list.add(new StatValueEntity("50.09","24/12/2017","49"));
        list.add(new StatValueEntity("51.98","20/12/2017","53"));
        list.add(new StatValueEntity("50.67","16/12/2017","54"));
        list.add(new StatValueEntity("49.3","12/12/2017","47"));
        list.add(new StatValueEntity("52.9","08/12/2017",null));
        list.add(new StatValueEntity("52.3","03/12/2017","51"));
        list.add(new StatValueEntity("52.3","01/12/2017","55"));

        for (String stat : stats) {
            st = new StatisticsEntity(stat, list);
            dao.insertAll(st);
        }

    }

    @Override
    public void onBackPressed() {
        // stops user going back to login screen
    }

    @Override
    public void onRestart() {
        super.onRestart();
        // closes app when home button is pressed
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}