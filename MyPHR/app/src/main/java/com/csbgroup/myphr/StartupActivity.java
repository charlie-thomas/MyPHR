package com.csbgroup.myphr;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import static com.csbgroup.myphr.R.*;
import static com.csbgroup.myphr.R.layout.*;

public class StartupActivity extends AppCompatActivity {
    String pin;
    EditText editText;
    public static final String PREFS = "pin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_startup);

        editText = findViewById(id.initialPin);

    }

    @SuppressLint("ApplySharedPref")
    public void changeButton(View view) {
        pin = editText.getText().toString();
        if (pin.equals("####") || pin.length() != 4) {
            Toast.makeText(this, "Invalid PIN", Toast.LENGTH_SHORT).show();
            editText.setText("");
        } else {
            SharedPreferences preferences = getSharedPreferences(PREFS,0);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("PIN", pin);
            editor.commit();

            Toast.makeText(this, "PIN changed", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }
}
