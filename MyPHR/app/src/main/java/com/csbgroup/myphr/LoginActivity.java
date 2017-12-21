package com.csbgroup.myphr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class LoginActivity extends AppCompatActivity {

    /* Button login_1, login_2, login_3, login_4, login_5, login_6, login_7, login_8, login_9, login_0;

    EditText login_pin; */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
/*
        login_1 = findViewById(R.id.login_1);
        login_2 = findViewById(R.id.login_2);
        login_3 = findViewById(R.id.login_3);
        login_4 = findViewById(R.id.login_4);
        login_5 = findViewById(R.id.login_5);
        login_6 = findViewById(R.id.login_6);
        login_7 = findViewById(R.id.login_7);
        login_8 = findViewById(R.id.login_8);
        login_9 = findViewById(R.id.login_9);
        login_0 = findViewById(R.id.login_0);

        login_pin = (EditText) findViewById(R.id.login_pin);

        login_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_pin.setText(login_pin.getText() + "1");
            }
        });

        login_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_pin.setText(login_pin.getText() + "2");
            }
        });

        login_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_pin.setText(login_pin.getText() + "3");
            }
        });

        login_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_pin.setText(login_pin.getText() + "4");
            }
        });

        login_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_pin.setText(login_pin.getText() + "5");
            }
        });

        login_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_pin.setText(login_pin.getText() + "6");
            }
        });

        login_7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_pin.setText(login_pin.getText() + "7");
            }
        });

        login_8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_pin.setText(login_pin.getText() + "8");
            }
        });

        login_9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_pin.setText(login_pin.getText() + "9");
            }
        });

        login_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_pin.setText(login_pin.getText() + "0");
            }
        });         */
    }


    public void goToMain(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}

