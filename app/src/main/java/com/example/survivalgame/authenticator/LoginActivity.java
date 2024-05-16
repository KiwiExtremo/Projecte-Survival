package com.example.survivalgame.authenticator;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.survivalgame.GameActivity;
import com.example.survivalgame.R;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 1;
    private EditText etUserMail, etPassword;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    private BeginSignInRequest signInRequest;
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
            etPassword.setError(getString(R.string.edit_text_error_password));
            progressBar.setVisibility(View.GONE);
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);
            if (task.isSuccessful()) {
                Toast.makeText(getApplicationContext(), getString(R.string.toast_login_successful), Toast.LENGTH_SHORT).show();

                startGameActivity();

            } else {
                // TODO check if auth fail is due to user not being in the database, password not patching user's, or other
                Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();

                // (TEMPORARY FIX)
                Intent i = new Intent(this, GameActivity.class);

                startActivity(i);
            }
        });
    }

    public void bOnClickStartRegisterActivity(View view) {
        Intent i = new Intent(this, RegisterActivity.class);
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

                if(result.getResultCode() == Activity.RESULT_OK) {

                    Intent intent = result.getData();
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
                    try {
                        // Google Sign In was successful, authenticate with Firebase
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        Toast.makeText(getApplicationContext(), "firebaseAuthWithGoogle:" + account.getId(), Toast.LENGTH_SHORT).show();
                        //Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                        firebaseAuthWithGoogle(account.getIdToken());
                    } catch (ApiException e) {
                        // Google Sign In failed, update UI appropriately
                        Toast.makeText(getApplicationContext(), "Google sign in failed"+ e.toString() ,Toast.LENGTH_LONG).show();

                        //Log.w(TAG, "Google sign in failed", e);
                    }
                }
            }
        });
    }

    public void bOnClickSignInWithGoogle(View view) {
        resultLauncher.launch(new Intent(mGoogleSignInClient.getSignInIntent()));
    }

    private void startGameActivity() {
        Intent i = new Intent(this, GameActivity.class);

        startActivity(i);

        finish();
    }

    private void firebaseAuthWithGoogle(String idToken) {
        // TODO update toasts
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                startGameActivity();

                // Sign in success, update UI with the signed-in user's information
                Toast.makeText(getApplicationContext(), "signInWithCredential:success", Toast.LENGTH_SHORT).show();

                // TODO use user's information and pass it to the game activity
                FirebaseUser user = mAuth.getCurrentUser();

            } else {
                // If sign in fails, display a message to the user.
                Toast.makeText(getApplicationContext(), "signInWithCredential:failure", Toast.LENGTH_SHORT).show();
                //Log.w(TAG, "signInWithCredential:failure", task.getException());
            }
        });
    }

    private void fetchFromActivity() {
        etUserMail = findViewById(R.id.etUsername);
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