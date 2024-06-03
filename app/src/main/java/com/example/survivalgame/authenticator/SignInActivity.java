package com.example.survivalgame.authenticator;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.survivalgame.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {

    private EditText etUsername, etPassword, etPasswordConfirm;
    private TextInputLayout etPasswordLayout, etPasswordConfirmLayout;
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
        setContentView(R.layout.activity_signin);

        fetchFromLayout();

        bRegister.setOnClickListener(view -> {
            bOnClickRegisterUser();
        });

        goToLogin.setOnClickListener(view -> {
            bOnClickStartLoginActivity();
        });
    }

    private void bOnClickStartLoginActivity() {
        Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
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
            etPasswordLayout.setError(getString(R.string.edit_text_error_password));
            etPassword.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    etPasswordLayout.setError(null);
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            progressBar.setVisibility(View.GONE);
            return;
        }

        if ("".equals(passwordConfirm)) {
            etPasswordConfirmLayout.setError(getString(R.string.edit_text_error_password));
            etPasswordConfirm.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    etPasswordConfirmLayout.setError(null);
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            progressBar.setVisibility(View.GONE);
            return;
        }

        if (!password.equals(passwordConfirm)) {
            etPasswordConfirmLayout.setError(getString(R.string.edit_text_error_confirm_password));
            etPasswordConfirm.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    etPasswordConfirmLayout.setError(null);
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            progressBar.setVisibility(View.GONE);
            return;
        }

        createUserWithMAuth(email, password);
    }


    private void createUserWithMAuth(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);
            if (task.isSuccessful()) {

                Toast.makeText(SignInActivity.this, "Acount Created.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();

            } else {
                // If registering fails, display a message to the user.
                Toast.makeText(SignInActivity.this, "Failed to register.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchFromLayout() {
        mAuth = FirebaseAuth.getInstance();
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etPasswordLayout = findViewById(R.id.etPasswordLayout);
        etPasswordConfirm = findViewById(R.id.etPasswordConfirm);
        etPasswordConfirmLayout = findViewById(R.id.etPasswordConfirmLayout);
        bRegister = findViewById(R.id.bSinglePlayer);
        progressBar = findViewById(R.id.progresBar);
        goToLogin = findViewById(R.id.tvAlreadyRegistered);
    }
}