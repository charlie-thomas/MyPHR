package com.csbgroup.myphr;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Toast toast = Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT);
                switch (item.getItemId()) {
                    case R.id.contacts:
                        toast.setText("Contacts Clicked");
                        toast.show();
                        break;
                    case R.id.medicine:
                        toast.setText("Medicine Clicked");
                        toast.show();
                        break;
                    case R.id.calendar:
                        toast.setText("Calendar Clicked");
                        toast.show();
                        break;
                    case R.id.appointments:
                        toast.setText("Appointments Clicked");
                        toast.show();
                        break;
                    case R.id.statistics:
                        toast.setText("Statistics Clicked");
                        toast.show();
                        break;
                }
                return true;
            }
        });
    }
}