package com.csbgroup.myphr;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import static com.csbgroup.myphr.R.*;

public class QuestionsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
    }
}
