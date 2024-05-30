package com.example.survivalgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.survivalgame.authenticator.LoginActivity;
import com.example.survivalgame.authenticator.RegisterActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.net.URISyntaxException;

public class BasicHomeActivity extends AppCompatActivity {

    private Button bSingle, bMulti, bLogOut;

    private TextView username, ally;

    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;

    private static final String PORT = "8080";
    private static final String SERVER_URI = "https://localhost:" + PORT;
    private P2PClient p2pClient;

    private boolean isRoomFound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_home);
        initialize();
        //onClickListeners();
        //getData();
    }



    private void onClickListeners() {
        //playListener();
        //logOutListener();
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
/*      bSingle = findViewById(R.id.bSinglePlayer);
        bMulti = findViewById(R.id.bMultiPlayer);*/
        bLogOut = findViewById(R.id.bLogOut1);
        //firebaseAuth = FirebaseAuth.getInstance();
        //user = firebaseAuth.getCurrentUser();
        username = findViewById(R.id.tvUser1);
        ally = findViewById(R.id.tvUser2);

        initializeSockets();
    }

    private void initializeSockets() {
        try {
            p2pClient = new P2PClient(SERVER_URI);
            p2pClient.connect();
            waitForConnectionAndSearchRoom();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }


    private void waitForConnectionAndSearchRoom() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (p2pClient != null && p2pClient.isOpen()) {
                    Log.d("MainActivity", "Connection established, searching for room...");
                    p2pClient.searchRoom();
                } else {
                    Log.d("MainActivity", "Waiting for connection...");
                    waitForConnectionAndSearchRoom(); // Reintentar hasta que la conexión esté abierta
                }
            }
        }, 1000); // Verificar cada segundo
    }

    private void createRoom() {
        if (p2pClient != null && p2pClient.isOpen()) {
            p2pClient.createRoom();
            Log.d("MainActivity", "Room created, waiting for another player....");
        } else {
            Log.d("MainActivity", "Waiting for connection to create room...");
            new Handler(Looper.getMainLooper()).postDelayed(this::createRoom, 1000); // Reintentar hasta que la conexión esté abierta
        }
    }
}