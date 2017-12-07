package com.csbgroup.myphr;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;

import com.csbgroup.myphr.database.AppDatabase;
import com.csbgroup.myphr.database.AppointmentsEntity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Populate database with data for debug purposes
        new Thread(new Runnable() {
            @Override
            public void run() {
                AppDatabase.getAppDatabase(MainActivity.this).appointmentsDao().deleteAll();
                populateAppointments(AppDatabase.getAppDatabase(MainActivity.this));
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
    public void setToolbar(String title) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
    }

    /* Helper function to switch the current fragment in the frame */
    public void switchFragment(Fragment newFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, newFragment);
        transaction.commit();
    }

    private static void populateAppointments(AppDatabase db)  {
        for (int i = 1; i < 6; i++) {
            AppointmentsEntity ae = new AppointmentsEntity();
            ae.setUid(i);
            ae.setTitle("Appointment " + i);
            ae.setDescription("Appointment description " + i);
            ae.setReminders(0);
            ae.setNotes("Appointment notes " + i);

            db.appointmentsDao().insertAll(ae);

            Log.d("DB", "Size" + db.appointmentsDao().getAll().size());
        }
    }
}