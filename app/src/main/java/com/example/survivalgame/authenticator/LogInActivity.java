package com.example.survivalgame.authenticator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.survivalgame.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LogInActivity extends AppCompatActivity {
    private EditText etUserMail, etPassword;
    private TextInputLayout etPasswordLayout;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton signInButton;
    private ActivityResultLauncher<Intent> resultLauncher;

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
        setContentView(R.layout.activity_login);

        fetchFromActivity();
        initializeGoogleSignInOptions();
        createActivityForResultLauncher();

        signInButton.setOnClickListener(view -> {
            bOnClickSignInWithGoogle(view);
        });
    }

    public void bOnClickStartLoginActivity(View view) {
        progressBar.setVisibility(View.VISIBLE);

        String email, password;

        email = String.valueOf(etUserMail.getText());
        password = String.valueOf(etPassword.getText());

        if ("".equals((email))) {
            etUserMail.setError(getString(R.string.edit_text_error_user_email));
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

        loginWithMAuth(email, password);
    }

    private void loginWithMAuth(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);
            if (task.isSuccessful()) {
                startMainActivity();

            } else {
                Toast.makeText(this, getString(R.string.toast_login_auth_failed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void bOnClickStartRegisterActivity(View view) {
        Intent i = new Intent(this, SignInActivity.class);
        startActivity(i);
    }

    public void bOnClickResetPasswordActivity(View view) {
        Intent i = new Intent(this, ResetPasswordActivity.class);
        startActivity(i);
    }

    private void createActivityForResultLauncher() {
        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
                    try {
                        // Google Sign In was successful, authenticate with Firebase
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        firebaseAuthWithGoogle(account.getIdToken());
                    } catch (ApiException e) {
                        // Google Sign In failed, update UI appropriately
                        Toast.makeText(getApplicationContext(), getString(R.string.toast_login_auth_failed), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    public void bOnClickSignInWithGoogle(View view) {
        resultLauncher.launch(new Intent(mGoogleSignInClient.getSignInIntent()));
    }

    private void startMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                startMainActivity();
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.toast_login_auth_failed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchFromActivity() {
        etUserMail = findViewById(R.id.etUsername);
        etPasswordLayout = findViewById(R.id.etPasswordLayout);
        etPassword = findViewById(R.id.etPassword);
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBarLogin);
        signInButton = findViewById(R.id.bGoogleLogin);
    }

    private void initializeGoogleSignInOptions() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_sign_in_default_web_client))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }
}
