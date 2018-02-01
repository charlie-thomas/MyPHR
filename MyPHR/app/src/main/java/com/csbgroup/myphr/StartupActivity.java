package com.csbgroup.myphr;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;

import static com.csbgroup.myphr.R.layout.*;

public class StartupActivity extends AppCompatActivity {
    public static final String TAG = "PinLockView";
    PinLockView mPinLockView_start;
    IndicatorDots mIndicatorDots_start;
    public static final String PREFS = "pin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_startup);

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
            Log.d(TAG, "PIN empty");
        }

        @Override
        public void onPinChange(int pinLength, String intermediatePin) {
            Log.d(TAG, "Pin changed, new length " + pinLength + " with intermediate pin " + intermediatePin);
        }
    };
}
