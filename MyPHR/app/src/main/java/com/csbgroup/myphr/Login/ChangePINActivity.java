package com.csbgroup.myphr.Login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.andrognito.pinlockview.PinLockView;
import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.csbgroup.myphr.R;

public class ChangePINActivity extends AppCompatActivity {
    PinLockView mPinLockView_change;
    IndicatorDots mIndicatorDots_change;
    public static final String PREFS = "pin";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pin);

        mPinLockView_change = findViewById(R.id.pin_change_entry);
        mPinLockView_change.setPinLockListener(mPinLockListener);
        mPinLockView_change = findViewById(R.id.pin_change_entry);
        mIndicatorDots_change = findViewById(R.id.indicator_dots_change);
        mPinLockView_change.attachIndicatorDots(mIndicatorDots_change);
        mPinLockView_change.setPinLockListener(mPinLockListener);
        mPinLockView_change.setPinLength(4);
        mPinLockView_change.setTextColor(ContextCompat.getColor(this, R.color.white));
        mIndicatorDots_change.setIndicatorType(IndicatorDots.IndicatorType.FIXED);

    }


    // Listener functions for the PIN input buttons
    @SuppressLint("ApplySharedPref")
    private PinLockListener mPinLockListener = new PinLockListener() {
        @Override
        public void onComplete(String pin) {

            SharedPreferences preferences = getSharedPreferences(PREFS, 0);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("PIN", pin);
            editor.commit();

            Toast.makeText(getApplicationContext(), "PIN changed", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }


        @Override
        public void onEmpty() {
        }


        @Override
        public void onPinChange(int pinLength, String intermediatePin) {
        }
    };
}
