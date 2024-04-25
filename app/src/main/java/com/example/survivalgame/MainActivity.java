package com.example.survivalgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsAnimationController;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private EditText etUsername, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fetchFromActivity();


    }

    public void bStartGameActivity(View view) {
        Window window = getWindow();

        final WindowInsetsController insetsController = getWindow().getInsetsController();
        if (insetsController != null) {
            insetsController.hide(WindowInsets.Type.statusBars());
        }

        setContentView(new Game(this));
    }
    public void bStartLoginActivity(View view) {
        
    }

    public void bStartRegisterActivity(View view) {
        Intent i = new Intent(this, RegisterActivity.class);

        startActivity(i);
    }

    private void fetchFromActivity() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
    }
}