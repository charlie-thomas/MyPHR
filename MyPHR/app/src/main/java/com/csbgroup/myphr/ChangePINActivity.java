package com.csbgroup.myphr;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.andrognito.pinlockview.PinLockView;
import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;

public class ChangePINActivity extends AppCompatActivity {
    EditText editText;
    public static final String TAG = "PinLockView";
    PinLockView mPinLockView;
    IndicatorDots mIndicatorDots;
    public static final String PREFS = "pin";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pin);

        editText = findViewById(R.id.pinChangeEntry);

        mPinLockView = findViewById(R.id.pin_lock_view);
        mPinLockView.setPinLockListener(mPinLockListener);
        mPinLockView = findViewById(R.id.pin_lock_view);
        mIndicatorDots = findViewById(R.id.indicator_dots);
        mPinLockView.attachIndicatorDots(mIndicatorDots);
        mPinLockView.setPinLockListener(mPinLockListener);
        mPinLockView.setPinLength(4);
        mPinLockView.setTextColor(ContextCompat.getColor(this, R.color.white));
        mIndicatorDots.setIndicatorType(IndicatorDots.IndicatorType.FIXED);

    }

    @SuppressLint("ApplySharedPref")
    private PinLockListener mPinLockListener = new PinLockListener() {
        @Override
        public void onComplete(String pin) {
            pin = editText.getText().toString();

            SharedPreferences preferences = getSharedPreferences(PREFS,0);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("PIN", pin);
            editor.commit();

            Toast.makeText(ChangePINActivity.this, "PIN changed", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(ChangePINActivity.this, ChangePINActivity.class);
            startActivity(intent);
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
