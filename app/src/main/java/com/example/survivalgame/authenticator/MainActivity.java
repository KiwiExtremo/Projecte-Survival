package com.example.survivalgame.authenticator;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.survivalgame.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private Button logOut;
    private TextView userTextView;
    private FirebaseUser user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initialize();
        button();
    }

    private void button() {
        logOut.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void initialize() {
        firebaseAuth = FirebaseAuth.getInstance();
        logOut = findViewById(R.id.bLogOut);
        userTextView = findViewById(R.id.tvUser);
        user = firebaseAuth.getCurrentUser();

        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            userTextView.setText(user.getEmail());
            // TODO no hacer nada, para que el usuario pueda elegir que hacer (log out, ver puntuaciones, etc)
        }

    }
}