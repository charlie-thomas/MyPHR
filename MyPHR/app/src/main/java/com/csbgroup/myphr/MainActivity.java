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
import com.csbgroup.myphr.database.MedicineDao;
import com.csbgroup.myphr.database.MedicineEntity;
import com.csbgroup.myphr.database.StatisticsDao;
import com.csbgroup.myphr.database.StatisticsEntity;

import java.util.ArrayList;

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
                populateMedicine(db.medicineDao());
                populateAppointments(db.appointmentsDao());
                populateStatistics(db.statisticsDao());
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
                        selectedPage = Calendar.newInstance();
                        break;
                    case R.id.appointments:
                        selectedPage = Appointments.newInstance();
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
        switchFragment(Calendar.newInstance());
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

    private static void populateMedicine(MedicineDao dao) {
        dao.deleteAll();

        String[] meds = {"Growth Hormone", "Oestrogen", "Progesterone", "Thyroxine"};

        for (String med : meds) {
            MedicineEntity me = new MedicineEntity(
                    med,
                    med + " Description",
                    med + " Notes",
                    true);
            dao.insertAll(me);
        }
    }


    private static void populateAppointments(AppointmentsDao dao)  {
        dao.deleteAll();

        for (int i = 1; i < 6; i++) {
            AppointmentsEntity ae = new AppointmentsEntity();
            ae.setTitle("Appointment " + i);
            ae.setDescription("Appointment description " + i);
            ae.setReminders(0);
            ae.setNotes("Appointment notes " + i);

            dao.insertAll(ae);
        }
    }

    private static void populateStatistics(StatisticsDao dao) {
        dao.deleteAll();

        String[] stats = {"Height Velocity", "Weight", "BMI"};
        ArrayList<String> list = new ArrayList<String>();
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