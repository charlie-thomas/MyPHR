package com.csbgroup.myphr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import static com.csbgroup.myphr.R.layout.*;

public class ForgotPINActivity extends AppCompatActivity {
    String answer1;
    String answer2;
    EditText answer1Text;
    EditText answer2Text;
    public static final String PREFS = "answers";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_forgot_pin);

        answer1Text = findViewById(R.id.attempt_1_answer);
        answer2Text = findViewById(R.id.attempt_2_answer);
    }

    public void forgotButton(View view) {
        SharedPreferences preferences = getSharedPreferences(PREFS,0);

        answer1 = answer1Text.getText().toString().toLowerCase();
        answer2 = answer2Text.getText().toString().toLowerCase();

        String storedAnswer1 = preferences.getString("answer1", "####");
        String storedAnswer2 = preferences.getString("answer2", "####");

        if (storedAnswer1.equals(answer1) && storedAnswer2.equals(answer2)) {
            Intent intent = new Intent(this, ChangePINActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Answers incorrect", Toast.LENGTH_SHORT).show();
            answer1Text.setText("");
            answer2Text.setText("");
        }

    }
}
