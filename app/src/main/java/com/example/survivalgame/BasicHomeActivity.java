package com.example.survivalgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.survivalgame.authenticator.LoginActivity;
import com.example.survivalgame.authenticator.RegisterActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class BasicHomeActivity extends AppCompatActivity {

    private Button bSingle, bMulti, bLogOut;

    private TextView username, ally;

    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_home);
        initialize();
        onClickListeners();
        getData();
    }



    private void onClickListeners() {
        playListener();
        logOutListener();
    }

    private void playListener() {
        bSingle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username, ally;
                username = String.valueOf(BasicHomeActivity.this.username.getText());
                ally = String.valueOf(BasicHomeActivity.this.ally.getText());

                if(!ally.equals("User2")){
                    Intent i = new Intent(getApplicationContext(), GameActivity.class);
                    startActivity(i);
                }
            }
        });
    }

    private void logOutListener() {
        bLogOut.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    //TODO get Username player and get Username player2
    private void getData() {
    }

    private void initialize() {
/*        bSingle = findViewById(R.id.bSinglePlayer);
        bMulti = findViewById(R.id.bMultiPlayer);*/
        bLogOut = findViewById(R.id.bLogOut1);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        username = findViewById(R.id.tvUser1);
        ally = findViewById(R.id.tvUser2);
    }
}