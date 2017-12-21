package com.csbgroup.myphr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // find the elements
        Button login_1 = findViewById(R.id.login_1);
        Button login_2 = findViewById(R.id.login_2);
        Button login_3 = findViewById(R.id.login_3);
        Button login_4 = findViewById(R.id.login_4);
        Button login_5 = findViewById(R.id.login_5);
        Button login_6 = findViewById(R.id.login_6);
        Button login_7 = findViewById(R.id.login_7);
        Button login_8 = findViewById(R.id.login_8);
        Button login_9 = findViewById(R.id.login_9);
        Button login_0 = findViewById(R.id.login_0);

        // set a listener
        login_1.setOnClickListener(this);
        login_2.setOnClickListener(this);
        login_3.setOnClickListener(this);
        login_4.setOnClickListener(this);
        login_5.setOnClickListener(this);
        login_6.setOnClickListener(this);
        login_7.setOnClickListener(this);
        login_8.setOnClickListener(this);
        login_9.setOnClickListener(this);
        login_0.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case login_1:
                textView.append("1");
                break;
            case login_2:
                textView.append("2");
                break;
            case login_3:
                textView.append("3");
                break;
            case login_4:
                textView.append("4");
                break;
            case login_5:
                textView.append("5");
                break;
            case login_6:
                textView.append("6");
                break;
            case login_7:
                textView.append("7");
                break;
            case login_8:
                textView.append("8");
                break;
            case login_9:
                textView.append("9");
                break;
            case login_0:
                textView.append("0");
                break;

        }
    }


    public void goToMain(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
