package com.csbgroup.myphr;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.csbgroup.myphr.Appointments.AppointmentsSection;
import com.csbgroup.myphr.Calendar.CalendarMonth;
import com.csbgroup.myphr.Contacts.Contacts;
import com.csbgroup.myphr.Login.LoginActivity;
import com.csbgroup.myphr.Medicine.MedicineSection;
import com.csbgroup.myphr.Statistics.Statistics;

public class MainActivity extends AppCompatActivity {

    public BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the bottom nav bar view
        bottomNavigationView = findViewById(R.id.bottom_nav);

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

                switchFragment(selectedPage, false);
                return true;
            }
        });

        // Show calendar when app first loads
        switchFragment(CalendarMonth.newInstance(), false);
        bottomNavigationView.setSelectedItemId(R.id.calendar);
    }


    /**
     * setToolbar is a helper function to set the title displayed in the toolbar.
     * @param title is the title to be shown
     * @param back is whether the back button is present in the toolbar
     */
    public void setToolbar(String title, boolean back) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(back);
        getSupportActionBar().setDisplayShowHomeEnabled(back);
    }


    /**
     * switchFragment is a helper function to change the fragment in the frame
     * @param newFragment is the fragment to switch to
     * @param backStack is whether to add the fragment transaction to the backstack
     */
    public void switchFragment(Fragment newFragment, boolean backStack) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, newFragment, newFragment.toString().split("\\{")[0]);

        for(int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); ++i)
            getSupportFragmentManager().popBackStack();

        if (backStack) transaction.addToBackStack(newFragment.getTag());
        transaction.commit();
    }


    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            if (fm.getBackStackEntryAt(0).getName().equals("AppointmentsDetails")) {
                fm.popBackStack();
                switchFragment(AppointmentsSection.newInstance(), false);
            } else if (fm.getBackStackEntryAt(0).getName().equals("MedicineDetails")) {
                fm.popBackStack();
                switchFragment(MedicineSection.newInstance(), false);
            } else fm.popBackStack();
        } else super.onBackPressed();
    }


    @Override
    public void onRestart() {
        super.onRestart();
        // closes app when home button is pressed
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}