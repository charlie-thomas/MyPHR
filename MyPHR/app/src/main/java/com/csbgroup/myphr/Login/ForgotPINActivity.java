package com.csbgroup.myphr.Login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.csbgroup.myphr.R;

import static com.csbgroup.myphr.R.layout.*;

public class ForgotPINActivity extends AppCompatActivity {
    String answer1;
    String answer2;
    EditText answer1Text;
    EditText answer2Text;
    TextView chosen1;
    TextView chosen2;

    public static final String PREFS = "answers";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_forgot_pin);

        SharedPreferences preferences = getSharedPreferences(PREFS, 0);

        answer1Text = findViewById(R.id.attempt_1_answer);
        answer2Text = findViewById(R.id.attempt_2_answer);

        chosen1 = findViewById(R.id.chosen1);
        chosen1.setText(preferences.getString("chosen1", "####"));

        chosen2 = findViewById(R.id.chosen2);
        chosen2.setText(preferences.getString("chosen2", "####"));
    }


    // Checks user input against stored answers and redirects to the
    // change PIN activity if there is a match.
    public void forgotButton(View view) {
        SharedPreferences preferences = getSharedPreferences(PREFS, 0);

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
