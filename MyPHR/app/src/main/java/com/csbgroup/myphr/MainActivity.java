package com.csbgroup.myphr;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
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
                        selectedPage = CalendarDay.newInstance();
                        break;
                    case R.id.appointments:
                        selectedPage = Appointments.newInstance();
                        break;
                    case R.id.statistics:
                        selectedPage = Statistics.newInstance();
                        break;
                }

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, selectedPage);
                transaction.commit();

                return true;
            }
        });

        // Show calendar when app first loads
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, CalendarDay.newInstance());
        transaction.commit();
    }
}