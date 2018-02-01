package com.csbgroup.myphr;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import static com.csbgroup.myphr.R.*;
import static com.csbgroup.myphr.R.layout.*;

public class QuestionsActivity extends AppCompatActivity implements OnItemSelectedListener {
    String answer1;
    String answer2;
    EditText answer1Text;
    EditText answer2Text;
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

        // Security question 2 spinner
        Spinner spinner2 = findViewById(id.security_2);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                array.questions_array, layout.spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);

        answer1Text = findViewById(id.security_1_answer);
        answer2Text = findViewById(id.security_2_answer);

    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        String chosen = parent.getItemAtPosition(pos).toString();
        Toast.makeText(this, "Question: " + chosen, Toast.LENGTH_SHORT).show();
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    @SuppressLint("ApplySharedPref")
    public void questionsButton(View view) {
        answer1 = answer1Text.getText().toString().toLowerCase();
        answer2 = answer2Text.getText().toString().toLowerCase();

        if (answer1.equals("") || answer2.equals("")) {
            Toast.makeText(this, "Please answer the security questions", Toast.LENGTH_SHORT).show();
        } else {
            SharedPreferences preferences = getSharedPreferences(PREFS, 0);
            SharedPreferences.Editor editor = preferences.edit();

            editor.putString("answer1", answer1);
            editor.putString("answer2", answer2);
            editor.commit();

            Toast.makeText(this, "Security questions set", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }
}
