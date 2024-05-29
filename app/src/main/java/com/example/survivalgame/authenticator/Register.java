package com.example.survivalgame.authenticator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.survivalgame.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {

    private EditText etUsername, etPassword, etPasswordConfirm;
    private Button bRegister;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private TextView goToLogin;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fetchFromLayout();

        bRegister.setOnClickListener(view -> {
            bOnClickRegisterUser();
        });

        goToLogin.setOnClickListener(view -> {
            bOnClickStartLoginActivity();
        });
    }

    private void bOnClickStartLoginActivity() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void bOnClickRegisterUser() {
        progressBar.setVisibility(View.VISIBLE);
        String email, password, passwordConfirm;

        email = etUsername.getText().toString();
        password = etPassword.getText().toString();
        passwordConfirm = etPasswordConfirm.getText().toString();

        if ("".equals(email)) {
            etUsername.setError(getString(R.string.edit_text_error_user_email));
            progressBar.setVisibility(View.GONE);
            return;
        }

        if ("".equals(password)) {
            etPassword.setError(getString(R.string.edit_text_error_password));
            progressBar.setVisibility(View.GONE);
            return;
        }

        if ("".equals(passwordConfirm)) {
            etPasswordConfirm.setError(getString(R.string.edit_text_error_password));
            progressBar.setVisibility(View.GONE);
            return;
        }

        createUserWithMAuth(email, password);
    }

    private void createUserWithMAuth(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);
            if (task.isSuccessful()) {

                Toast.makeText(Register.this, "Acount Created.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();

            } else {
                // If registering fails, display a message to the user.
                Toast.makeText(Register.this, "Failed to register.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchFromLayout() {
        mAuth = FirebaseAuth.getInstance();
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etPasswordConfirm = findViewById(R.id.etPasswordConfirm);
        bRegister = findViewById(R.id.bRegister);
        progressBar = findViewById(R.id.progresBar);
        goToLogin = findViewById(R.id.tvAlreadyRegistered);
    }

}