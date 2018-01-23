package com.csbgroup.myphr;

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

        // Populate database with data for debug purposes
        new Thread(new Runnable() {
            @Override
            public void run() {
                AppDatabase db = AppDatabase.getAppDatabase(MainActivity.this);
                populateContacts(db.contactsDao());
                populateMedicine(db.medicineDao());
                populateAppointments(db.appointmentsDao());
                populateStatistics(db.statisticsDao());
                populateInvestigations(db.investigationDao());
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
                        selectedPage = Medicine.newInstance();
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

        // TODO: get contact details for main staff and load in

        ContactsEntity c1 = new ContactsEntity("Dr. Doctor", "drdoctor@hospital.com",
                "012334567890", "My main doctor at hospital.");

        ContactsEntity c2 = new ContactsEntity("Mr. Nurse", "mrnurse@hospital.com",
                "015567892343", "My main nurse at hospital.");

        dao.insertAll(c1,c2);

    }

    private static void populateMedicine(MedicineDao dao) {
        dao.deleteAll();

        String[] meds = {"Growth Hormone", "Oestrogen", "Progesterone", "Thyroxine"};

        for (String med : meds) {
            MedicineEntity me = new MedicineEntity(
                    med,
                    med + " Description",
                    med + "Dose",
                    med + " Notes",
                    true);
            dao.insertAll(me);
        }
    }


    private static void populateAppointments(AppointmentsDao dao)  {
        dao.deleteAll();

        for (int i = 1; i < 6; i++) {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.add(Calendar.DATE, i);
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

            AppointmentsEntity ae = new AppointmentsEntity("Appointment " + i, "Appointment location " + i,
                    df.format(c.getTime()), "8","Appointment notes " + i, false);
            dao.insertAll(ae);
        }
    }

    private static void populateInvestigations(InvestigationsDao dao)  {
        dao.deleteAll();

        InvestigationsEntity ie = new InvestigationsEntity("Blood Test", "03/01/2018");
        InvestigationsEntity ie1 = new InvestigationsEntity("Hearing Test", "29/12/2017");
        InvestigationsEntity ie2 = new InvestigationsEntity("Blood Test", "04/06/2017");
        InvestigationsEntity ie3 = new InvestigationsEntity("Hearing Test", "30/06/2017");

        dao.insertAll(ie, ie1, ie2, ie3);
    }

    private static void populateStatistics(StatisticsDao dao) {
        dao.deleteAll();

        String[] stats = {"Blood Pressure", "Body Mass Index (BMI)", "Head Circumference", "Height",
                "Height Velocity", "Length", "Weight"};
        ArrayList<StatValueEntity> list = new ArrayList<StatValueEntity>();

        for (String stat : stats) {
            StatisticsEntity st = new StatisticsEntity(stat, list);
            dao.insertAll(st);
        }
    }

    @Override
    public void onBackPressed() {
        // stops user going back to login screen
    }
}