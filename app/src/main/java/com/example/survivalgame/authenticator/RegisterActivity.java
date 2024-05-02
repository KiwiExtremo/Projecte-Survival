package com.example.survivalgame.authenticator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.survivalgame.LoginActivity;
import com.example.survivalgame.R;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    public void bOnClickStartMainActivity() {
        // TODO check data is correct with webservice

        Intent i = new Intent(this, LoginActivity.class);
    }
}