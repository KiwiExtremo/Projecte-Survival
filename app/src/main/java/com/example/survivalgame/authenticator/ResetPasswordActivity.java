package com.example.survivalgame.authenticator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.survivalgame.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ResetPasswordActivity extends AppCompatActivity {
    private EditText etEmail;
    private TextView tvGoToLogin;
    private Button bSend;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pasword);

        initialize();

        bSend.setOnClickListener(view -> {
            bOnClickCheckEmailAndSendMail();
        });

        tvGoToLogin.setOnClickListener(view -> {
            startLoginActivity();
        });
    }

    private void bOnClickCheckEmailAndSendMail() {
        progressBar.setVisibility(View.VISIBLE);
        String email = etEmail.getText().toString();

        if ("".equals(email)) {
            etEmail.setError(getString(R.string.edit_text_error_user_email));
            progressBar.setVisibility(View.GONE);
        } else {
            sendRecoveryEmail(email);
        }
    }

    private void sendRecoveryEmail(String email) {
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);
            if (task.isSuccessful()) {
                showDialogEmailSent(email);
            } else {
                Toast.makeText(this, getString(R.string.toast_recovery_email_unsuccessful), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void startLoginActivity() {
        Intent i = new Intent(getApplicationContext(), LogInActivity.class);
        startActivity(i);
        finish();
    }

    private void showDialogEmailSent(String email) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.dialog_recovery_email_title));
        builder.setMessage(getString(R.string.dialog_recovery_email_body, email));
        builder.setPositiveButton(getString(R.string.dialog_recovery_email_positive), (dialog, which) -> {
            startLoginActivity();
        });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    private void initialize() {
        etEmail = findViewById(R.id.etResetEmail);
        bSend = findViewById(R.id.bResetPassword);
        progressBar = findViewById(R.id.progresBar);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);
        mAuth = FirebaseAuth.getInstance();
    }
}
