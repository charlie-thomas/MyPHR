package com.csbgroup.myphr;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ChangePINActivity extends AppCompatActivity {
    String pin;
    EditText editText;
    public static final String PREFS = "pin";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pin);

        editText = findViewById(R.id.pinChangeEntry);

    }

    @SuppressLint("ApplySharedPref")
    public void changeButton(View view) {
        pin = editText.getText().toString();

        SharedPreferences preferences = getSharedPreferences(PREFS,0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("PIN", pin);
        editor.commit();

        Toast.makeText(this, "PIN changed", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
