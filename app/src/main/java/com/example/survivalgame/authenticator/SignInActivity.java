package com.example.survivalgame.authenticator;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.survivalgame.R;
import com.example.survivalgame.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignInActivity extends AppCompatActivity {

    private EditText etEmail, etPassword, etPasswordConfirm;
    private TextInputLayout etPasswordLayout, etPasswordConfirmLayout;
    private Button bRegister;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private TextView goToLogin;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

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

        email = etEmail.getText().toString();
        password = etPassword.getText().toString();
        passwordConfirm = etPasswordConfirm.getText().toString();

        if ("".equals(email)) {
            etEmail.setError(getString(R.string.edit_text_error_user_email));
            progressBar.setVisibility(View.GONE);
            return;
        }

        if (!isValidEmail(email)) {
            etEmail.setError(getString(R.string.edit_text_error_invalid_email));
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
        addDatatoFirebase("<null>", email, 0);
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

    private void addDatatoFirebase(String name, String address, int puntuacion) {
        // Crear una instancia de la clase User y establecer sus atributos
        User user = new User();
        user.setUsername(name);
        user.setEmail(address.replace(".", "_"));
        user.setPuntuacion(puntuacion);

        // Formatear la dirección de email reemplazando los puntos con guiones bajos
        String formattedAddress = address.replace(".", "_");

        // Obtener la referencia al nodo específico del usuario en la base de datos
        DatabaseReference userReference = firebaseDatabase.getReference("Users").child(formattedAddress);

        // Establecer los valores en la referencia del usuario en la base de datos
        userReference.setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Mostrar un mensaje de éxito cuando los datos se agreguen correctamente
                        Toast.makeText(SignInActivity.this, "Usuario creado correctamente", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Mostrar un mensaje de error si la operación falla
                        Toast.makeText(SignInActivity.this, "Error al crear el usuario: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean isValidEmail(CharSequence email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void fetchFromLayout() {
        mAuth = FirebaseAuth.getInstance();
        etEmail = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etPasswordLayout = findViewById(R.id.etPasswordLayout);
        etPasswordConfirm = findViewById(R.id.etPasswordConfirm);
        etPasswordConfirmLayout = findViewById(R.id.etPasswordConfirmLayout);
        bRegister = findViewById(R.id.bSinglePlayer);
        progressBar = findViewById(R.id.progresBar);
        goToLogin = findViewById(R.id.tvAlreadyRegistered);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
    }
}