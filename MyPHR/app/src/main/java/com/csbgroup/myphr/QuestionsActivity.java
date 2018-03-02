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

public class QuestionsActivity extends AppCompatActivity {
    String answer1;
    String answer2;
    EditText answer1Text;
    EditText answer2Text;
    static String chosen1;
    static String chosen2;

    public static final String PREFS = "answers";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_questions);

        // Security question 1 spinner
        Spinner spinner = findViewById(id.security_1);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                array.questions_array, layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        chosen1 = spinner.getSelectedItem().toString();

        // Security question 2 spinner
        Spinner spinner2 = findViewById(id.security_2);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                array.questions_array_2, layout.spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);


        answer1Text = findViewById(id.security_1_answer);
        answer2Text = findViewById(id.security_2_answer);

    }

    @SuppressLint("ApplySharedPref")
    public void questionsButton(View view) {
        SharedPreferences preferences = getSharedPreferences(PREFS, 0);
        SharedPreferences.Editor editor = preferences.edit();

        Spinner spinner = findViewById(id.security_1);
        chosen1 = spinner.getSelectedItem().toString();
        editor.putString("chosen1", chosen1);


        Spinner spinner2 = findViewById(id.security_2);
        chosen2 = spinner2.getSelectedItem().toString();
        editor.putString("chosen2", chosen2);

        answer1 = answer1Text.getText().toString().toLowerCase();
        answer2 = answer2Text.getText().toString().toLowerCase();

        if (answer1.equals("") || answer2.equals("")) {
            Toast.makeText(this, "Please answer the security questions", Toast.LENGTH_SHORT).show();
        } else {

            editor.putString("answer1", answer1);
            editor.putString("answer2", answer2);
            editor.commit();

            Toast.makeText(this, "Security questions set", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }
}
