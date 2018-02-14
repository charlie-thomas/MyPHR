package com.csbgroup.myphr;

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

        // TODO: get contact details for main staff and load in
        dao.insertAll(new ContactsEntity(
                "Dr. Jones",
                "james.jones@hospital.com",
                "012334567890",
                "My main doctor at hospital."),
                new ContactsEntity(
                "Nurse Williams",
                "amy.williams@hospital.com",
                "012334567890",
                "My main nurse at hospital."
                ));
    }

    private static void populateMedicine(MedicineDao dao) {
        dao.deleteAll();

        dao.insertAll(new MedicineEntity(
                "Growth Hormone",
                "Natural Hormone to simulate growth.",
                "5mg",
                "Take once a day.",
                true,
                true,
                false,
                null,
                "04:15"),
                new MedicineEntity(
                "Oestrogen",
                "Helps in the development and maintenance of sexual maturation.",
                "2mg",
                "Tablets and patches should be taken once a day.",
                true,
                false,
                false,
                null,
                "02:10"),
                new MedicineEntity(
                "Progesterone",
                "Progesterone description",
                "2mg",
                "To be taken on 7-12 days of calendar month either monthly, every 2nd month or\n" +
                        "every 3rd month",
                true,
                false,
                true,
                "05/02/2018",
                "02:30"),
                new MedicineEntity(
                "Thyroxine",
                "Thyroxine description",
                "2mg",
                "To be taken daily",
                true,
                false,
                false,
                null,
                "01:15")
                );
    }


    private static void populateAppointments(AppointmentsDao dao)  {
        dao.deleteAll();

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

        c.add(Calendar.DATE, 1);
        dao.insertAll(new AppointmentsEntity("Clinic 1", "Children's Hospital",
                df.format(c.getTime()), "08:00","Appointment Notes", true));
        c.add(Calendar.DATE, 1);
        dao.insertAll(new AppointmentsEntity("Check Up 1", "Children's Hospital",
                df.format(c.getTime()), "08:00","Appointment Notes", true));
        c.add(Calendar.DATE, 1);
        dao.insertAll(new AppointmentsEntity("Check Up 2", "Children's Hospital",
                df.format(c.getTime()), "08:00","Appointment Notes", true));
        c.add(Calendar.DATE, 1);
        dao.insertAll(new AppointmentsEntity("Clinic 2", "Children's Hospital",
                df.format(c.getTime()), "08:00","Appointment Notes", true));
        c.add(Calendar.DATE, 1);
        dao.insertAll(new AppointmentsEntity("Check Up 3", "Children's Hospital",
                df.format(c.getTime()), "08:00","Appointment Notes", true));
    }

    private static void populateInvestigations(InvestigationsDao dao)  {
        dao.deleteAll();

        InvestigationsEntity ie = new InvestigationsEntity("Blood Test", "03/01/2018", "due in 6 months");
        InvestigationsEntity ie1 = new InvestigationsEntity("Hearing Test", "29/12/2017", "due in 12 months");
        InvestigationsEntity ie2 = new InvestigationsEntity("Blood Test", "04/06/2017", "due in 6 months");
        InvestigationsEntity ie3 = new InvestigationsEntity("Hearing Test", "30/06/2017", "due in 12 months");

        dao.insertAll(ie, ie1, ie2, ie3);
    }

    private static void populateStatistics(StatisticsDao dao) {
        dao.deleteAll();

        String[] stats = {"Blood Pressure", "Body Mass Index (BMI)", "Head Circumference", "Height",
                "Height Velocity", "Weight"};
        ArrayList<StatValueEntity> list = new ArrayList<StatValueEntity>();

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
            StatisticsEntity st = new StatisticsEntity(stat, list);
            dao.insertAll(st);
        }
    }

    @Override
    public void onBackPressed() {
        // stops user going back to login screen
    }

    @Override
    public void onPause() {
        super.onPause();
        // closes app when home button is pressed
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}