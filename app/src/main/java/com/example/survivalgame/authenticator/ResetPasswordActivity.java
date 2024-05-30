package com.example.survivalgame.authenticator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.survivalgame.R;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {
    private EditText etEmail;
    private Button bSend;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pasword);

        initialize();

        bSend.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            String email = etEmail.getText().toString();

            if ("".equals(email)) {
                etEmail.setError(getString(R.string.edit_text_error_user_email));
                progressBar.setVisibility(View.GONE);

            } else {
                resetPassword(email);
            }
        });
    }

    private void resetPassword(String email) {
        boolean emailInDB = checkEmailOnFirebase(email);

        if (emailInDB) {
            mAuth.sendPasswordResetEmail(etEmail.getText().toString()).addOnCompleteListener(task -> {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    showDialogEmailSent(email);

                } else {
                    Toast.makeText(this, getString(R.string.toast_recovery_email_unsuccessful), Toast.LENGTH_LONG).show();
                }
            });

        } else {
            // The dialog is shown, but the mail is not sent. The user will not know this, so as
            // to preserve as much privacy and sensitive data as possible
            showDialogEmailSent(email);
        }

    }

    private boolean checkEmailOnFirebase(String email) {
        // TODO checkear que en la DB sta el email

        return false;
    }

    private void startLoginActivity() {
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(i);
        finish();
    }

    private void showDialogEmailSent(String email) {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.dialog_recovery_email_title));

        builder.setMessage(getString(R.string.dialog_recovery_email_body, email));

        // add the buttons
        builder.setPositiveButton(getString(R.string.dialog_recovery_email_positive), (dialog, which) -> {
            startLoginActivity();

            finish();
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    private void initialize() {
        etEmail = findViewById(R.id.etResetEmail);
        bSend = findViewById(R.id.bResetPassword);
        progressBar = findViewById(R.id.progresBar);

        mAuth = FirebaseAuth.getInstance();
    }
}
