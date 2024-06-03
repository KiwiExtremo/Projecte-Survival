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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * The ResetPasswordActivity class is an activity that handles the recovery of the user's account if
 * they forget their password. The user is asked for an email, and sent a recovery mail if the mail
 * is inside the database. Afterwards, the user will have to follow the steps on the mail to create
 * a new password for their account.
 */
public class ResetPasswordActivity extends AppCompatActivity {
    private EditText etEmail;
    private TextView tvGoToLogin;
    private Button bSend;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;

    /**
     * onCreate() sets the content view, initializes the views, and sets the onClickListeners to the
     * buttons.
     *
     * @param savedInstanceState A saved instance state.
     */
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

    /**
     * bOnClickCheckEmailAndSendMail() checks that the email field on the view is not empty, and sets
     * an error if so. Otherwise, sends the given email to the {@link #checkEmailOnFirebase(String)}
     * method.
     */
    private void bOnClickCheckEmailAndSendMail() {
        progressBar.setVisibility(View.VISIBLE);
        String email = etEmail.getText().toString();

        if ("".equals(email)) {
            etEmail.setError(getString(R.string.edit_text_error_user_email));
            progressBar.setVisibility(View.GONE);

        } else {
            checkEmailOnFirebase(email);
        }
    }

    /**
     * checkEmailOnFirebase() gets a snapshot of the data from the database, and checks if the given
     * email is on the data. If the task is successful, the {@link #sendRecoveryEmail(String)} method
     * is called. Otherwise, the {@link #showDialogEmailSent(String)} method is called directly,
     * without actually sending the mail, so as to preserve as much sensitive data as possible from
     * malicious users.
     *
     * @param email the email to check on the Firebase database.
     */
    private void checkEmailOnFirebase(String email) {
        // TODO comprobar que esto chusca correctamente
        DatabaseReference emailReference = firebaseDatabase.getReference("Users");
        emailReference.child(email).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                sendRecoveryEmail(email);

            } else {
                // The dialog is shown, but the mail is not actually sent. The user will not know this, so as
                // to preserve as much privacy and sensitive data as possible
                showDialogEmailSent(email);
            }
        });
    }

    /**
     * sendRecoveryEmail() tries to send a mail to the given email through the Firebase API. If the
     * task is successful, the {@link #showDialogEmailSent(String)} method will be called. Otherwise,
     * a toast will be shown to the user.
     *
     * @param email the email to which the mail will be sent.
     */
    private void sendRecoveryEmail(String email) {
        mAuth.sendPasswordResetEmail(etEmail.getText().toString()).addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);
            if (task.isSuccessful()) {
                showDialogEmailSent(email);

            } else {
                Toast.makeText(this, getString(R.string.toast_recovery_email_unsuccessful), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * startLoginActivity() starts the {@link LogInActivity} and finishes the current Activity.
     */
    private void startLoginActivity() {
        Intent i = new Intent(getApplicationContext(), LogInActivity.class);
        startActivity(i);
        finish();
    }

    /**
     * showDialogEmailSent() builds a dialog to show the user that the recovery email has been sent.
     * The positive button will call {@link #startLoginActivity()} method.
     *
     * @param email the email to which the mail has been sent.
     */
    private void showDialogEmailSent(String email) {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.dialog_recovery_email_title));

        builder.setMessage(getString(R.string.dialog_recovery_email_body, email));

        // add the buttons
        builder.setPositiveButton(getString(R.string.dialog_recovery_email_positive), (dialog, which) -> {
            startLoginActivity();
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    /**
     * initialize() fetches the views from the layout, and instantiates the firebase elements.
     */
    private void initialize() {
        etEmail = findViewById(R.id.etResetEmail);
        bSend = findViewById(R.id.bResetPassword);
        progressBar = findViewById(R.id.progresBar);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);

        firebaseDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }
}
