package com.csbgroup.myphr.Login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.csbgroup.myphr.R;
import com.csbgroup.myphr.database.AppDatabase;
import com.csbgroup.myphr.database.ContactsDao;
import com.csbgroup.myphr.database.ContactsEntity;
import com.csbgroup.myphr.database.StatValueEntity;
import com.csbgroup.myphr.database.StatisticsDao;
import com.csbgroup.myphr.database.StatisticsEntity;

import java.util.ArrayList;

import static com.csbgroup.myphr.R.layout.*;

public class StartupActivity extends AppCompatActivity {
    PinLockView mPinLockView_start;
    IndicatorDots mIndicatorDots_start;
    public static final String PREFS = "pin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_startup);

        // Populate the database with the required contacts and statistics on start up
        new Thread(new Runnable() {
            @Override
            public void run() {
                AppDatabase db = AppDatabase.getAppDatabase(StartupActivity.this);
                populateContacts(db.contactsDao());
                populateStats(db.statisticsDao());
            }
        }).start();

        mPinLockView_start = findViewById(R.id.initial_pin);
        mPinLockView_start.setPinLockListener(mPinLockListener);
        mPinLockView_start = findViewById(R.id.initial_pin);
        mIndicatorDots_start = findViewById(R.id.indicator_dots_start);
        mPinLockView_start.attachIndicatorDots(mIndicatorDots_start);
        mPinLockView_start.setPinLockListener(mPinLockListener);
        mPinLockView_start.setPinLength(4);
        mPinLockView_start.setTextColor(ContextCompat.getColor(this, R.color.white));
        mIndicatorDots_start.setIndicatorType(IndicatorDots.IndicatorType.FIXED);
    }


    @SuppressLint("ApplySharedPref")
    private PinLockListener mPinLockListener = new PinLockListener() {
        @Override
        public void onComplete(String pin) {
            if (pin.equals("####") || pin.length() != 4) {
                Toast.makeText(getApplicationContext(), "Invalid PIN", Toast.LENGTH_SHORT).show();
            } else {
                SharedPreferences preferences = getSharedPreferences(PREFS,0);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("PIN", pin);
                editor.commit();

                Toast.makeText(getApplicationContext(), "PIN set", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), QuestionsActivity.class);
                startActivity(intent);
            }
        }

        @Override
        public void onEmpty() {
        }

        @Override
        public void onPinChange(int pinLength, String intermediatePin) {
        }
    };


    /* Helper function to populate contacts with staff */
    private void populateContacts(ContactsDao dao) {
        dao.deleteAll();
        // Key medical staff at Royal Childrens Hospital, Glasgow are pre-loaded
        dao.insertAll(
                new ContactsEntity(
                        "Dr Avril Mason",
                        "avrilmason@nhs.net",
                        "01412010000",
                        "Phone will go through to mobile.\n\nGround Floor, Zone 2, Office Block,\nRoyal Hospital for Children,\n" +
                                "Queen Elizabeth University Hospital,\nGovan Road,\nGlasgow G51 4TF"),

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

    /* Helper function to populate statistics section */
    private void populateStats(StatisticsDao dao) {
        dao.deleteAll();
        String[] stats = {"Blood Pressure", "Body Mass Index (BMI)", "Head Circumference", "Height",
                "Height Velocity", "Weight"};

        for (String s : stats)
            dao.insertAll(new StatisticsEntity(s, new ArrayList<StatValueEntity>()));
    }
}
