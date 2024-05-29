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

public class RegisterActivity extends AppCompatActivity {

    private EditText email, password, confirmPassword;
    private Button regist;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private TextView logNow;

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
        setContentView(R.layout.regist_activity);


        initialize();

        regist.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            String email, password, confirmPassword;
            email = String.valueOf(RegisterActivity.this.email.getText());
            password = String.valueOf(RegisterActivity.this.password.getText());
            confirmPassword = String.valueOf(RegisterActivity.this.confirmPassword.getText());

            if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                Toast.makeText(RegisterActivity.this, "Enter something", Toast.LENGTH_LONG);
                progressBar.setVisibility(View.INVISIBLE);
                return;
            }

            if(TextUtils.isEmpty(password)){
                Toast.makeText(RegisterActivity.this, "enter password", Toast.LENGTH_LONG);
                progressBar.setVisibility(View.INVISIBLE);
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.INVISIBLE);
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {

                                Toast.makeText(RegisterActivity.this, "Acount Created.",
                                        Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();

                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

        });

        logNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void initialize() {
        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.etRegistEmail);
        password = findViewById(R.id.etRegistPassword);
        confirmPassword = findViewById(R.id.etPasswordConfirm);
        regist = findViewById(R.id.bRegister);
        progressBar = findViewById(R.id.progresBarR);
        logNow = findViewById(R.id.tvLoginNow);

    }

}