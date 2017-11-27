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
                switch (item.getItemId()) {
                    case R.id.contacts:
                        Toast.makeText(MainActivity.this, "Contacts Clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.medicine:
                        Toast.makeText(MainActivity.this, "Medicine Clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.calendar:
                        Toast.makeText(MainActivity.this, "Calendar Clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.appointments:
                        Toast.makeText(MainActivity.this, "Contacts Clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.statistics:
                        Toast.makeText(MainActivity.this, "Contacts Clicked", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });
    }
}