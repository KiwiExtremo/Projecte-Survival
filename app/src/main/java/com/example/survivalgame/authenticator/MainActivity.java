package com.example.survivalgame.authenticator;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.survivalgame.GameActivity;
import com.example.survivalgame.R;
import com.example.survivalgame.multiplayer.WiFiDirectBroadcastReceiver;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    public static final int MODE_SINGLEPLAYER = 0;
    public static final int MODE_MULTIPLAYER = 1;
    private static final int PERMISSIONS_REQUEST_CODE = 1;
    private static final String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    private static final String[] REQUIRED_PERMISSIONS_API_33 = {Manifest.permission.NEARBY_WIFI_DEVICES};
    private static final int NO_ERROR = 0;
    private static final int USERNAME_ALREADY_EXISTS = 1;
    private FirebaseAuth firebaseAuth;
    private Button bLogOut, bStartSinglePlayer, bStartMultiplayer, bLeaderboards;
    private TextView tvUsername, tvTeammate;
    private FirebaseUser user;

    // P2P fields
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private BroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;
    //Firebase
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private boolean checkUsername = false;
    private boolean isUsernameChecked = false;
    private int usernameError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        initialize();
        setOnClickListeners();

        if (allPermissionsGranted()) {
            initializeWiFiP2P();

        } else {
            requestPermissions();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // API 33
            ActivityCompat.requestPermissions(this, concatArrays(REQUIRED_PERMISSIONS, REQUIRED_PERMISSIONS_API_33), PERMISSIONS_REQUEST_CODE);

        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
        }
    }

    private String[] concatArrays(String[] first, String[] second) {
        String[] result = new String[first.length + second.length];
        System.arraycopy(first, 0, result, 0, first.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        // Check an additional permission for API 33 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            for (String permission : REQUIRED_PERMISSIONS_API_33) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (allPermissionsGranted()) {
                initializeWiFiP2P();
            } else {
                Toast.makeText(this, getString(R.string.toast_error_no_permissions), Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @SuppressLint("MissingPermission")
    public void connectToPeer(WifiP2pDevice device) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;

        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                // Connection successful
            }

            @Override
            public void onFailure(int reason) {
                // Connection failed
            }
        });
    }

    public void startHosting() {
        // Code to start hosting a room
    }

    public void handleConnectionInfo(WifiP2pInfo info) {
        // Handle connection information and start communication with the other device
    }

    @SuppressLint("MissingPermission")
    private void startPeerDiscovery() {
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                // Peer discovery started
            }

            @Override
            public void onFailure(int reasonCode) {
                // Peer discovery failed
            }
        });
    }

    private void setOnClickListeners() {
        bLogOut.setOnClickListener(view -> bOnClickLogOut());
        bLeaderboards.setOnClickListener(view -> bOnClickShowLeaderboards());
        bStartSinglePlayer.setOnClickListener(view -> bOnClickStartSinglePlayer());
        bStartMultiplayer.setOnClickListener(view -> bOnClickStartMultiPlayer());
    }

    private void bOnClickLogOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
        startActivity(intent);
        finish();
    }

    private void bOnClickStartSinglePlayer() {
        Intent intent = new Intent(getApplicationContext(), GameActivity.class);
        intent.putExtra("Mode", MODE_SINGLEPLAYER);
        startActivity(intent);
    }

    private void bOnClickStartMultiPlayer() {
        Intent intent = new Intent(getApplicationContext(), GameActivity.class);
        intent.putExtra("Mode", MODE_MULTIPLAYER);
        startActivity(intent);
    }

    private void bOnClickShowLeaderboards() {
        // TODO fetch data from database and show it
        showDialogLeaderboards();
    }

    private void showDialogNewUser(String email) {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.dialog_pause_title));
        builder.setMessage(getString(R.string.dialog_pause_body));

        EditText etUsername = new EditText(this);
        etUsername.setSingleLine();
        etUsername.setHint("Nombre de usuario");

        LinearLayout llUsername = createWrappedLayout(etUsername);
        builder.setView(llUsername);

        // add the buttons
        builder.setPositiveButton(getString(R.string.dialog_pause_positive), (dialog, which) -> {

        });


        builder.setPositiveButton("Aceptar", (dialog, which) -> {
            String username = etUsername.getText().toString();
            if ("".equals(username)) {
                etUsername.setError("El nombre de usuario no puede estar vacío");
            } else {
                checkAndSetUsername(email, username);

                if (usernameError == NO_ERROR) {
                    dialog.dismiss();

                } else if (usernameError == USERNAME_ALREADY_EXISTS) {//TODO mirar porque no salta el setError
                    etUsername.setError("El nombre de usuario ya existe");
                }
            }
        });


        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCancelable(false);
    }

    private LinearLayout createWrappedLayout(EditText editText) {
        LinearLayout linearLayout = new LinearLayout(this);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER;

        editText.setLayoutParams(layoutParams);

        linearLayout.addView(editText);
        linearLayout.setPadding(60, 0, 60, 0);

        return linearLayout;
    }

    private void showDialogLeaderboards() {
        // TODO show leaderboards
    }

    private void initialize() {
        firebaseAuth = FirebaseAuth.getInstance();

        bLogOut = findViewById(R.id.bLogOut);
        bStartSinglePlayer = findViewById(R.id.bSinglePlayer);
        bStartMultiplayer = findViewById(R.id.bMultiPlayer);
        bLeaderboards = findViewById(R.id.bLeaderboards);
        tvUsername = findViewById(R.id.tvUsername);
        tvTeammate = findViewById(R.id.tvTeammate);
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
            startActivity(intent);
            finish();

        } else {
            String email = user.getEmail();

            if (checkIfUsernameIsNull(email)) {
                showDialogNewUser(user.getEmail());
            } else {
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            DataSnapshot puntuacionSnapshot = snapshot.child(email).child("username");
                            if (puntuacionSnapshot.exists() && puntuacionSnapshot.getValue() != null) {
                                String data = puntuacionSnapshot.getValue().toString();
                                tvUsername.setText(data);
                            } else {
                                Toast.makeText(MainActivity.this, "No se ha encontrado un usuario con este nombre", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        }
    }


    private void checkAndSetUsername(String email, String newUsername) {
        DatabaseReference usersReference = firebaseDatabase.getReference("Users");

        usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean usernameExists = false;

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String existingUsername = userSnapshot.child(email.replace(".", "_")).child("username").getValue(String.class);
                    if (newUsername.equals(existingUsername)) {
                        usernameExists = true;
                        break;
                    }
                }
                if (usernameExists) {
                    usernameError = USERNAME_ALREADY_EXISTS;

                } else {
                    tvUsername.setText(newUsername);
                    DatabaseReference nameReference = usersReference.child(email.replace(".", "_")).child("username");
                    nameReference.setValue(newUsername);
                    Toast.makeText(MainActivity.this, "Nombre de usuario actualizado", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkIfUsernameIsNull(String email) {
        DatabaseReference nameReference = firebaseDatabase.getReference("Users").child(email.replace(".", "_")).child("username");

        nameReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot dataSnapshot = task.getResult();
                if (dataSnapshot.exists()) {
                    String currentUsername = dataSnapshot.getValue(String.class);
                    if ("<null>".equals(currentUsername)) {
                        checkUsername = true;
                    } else {
                        Toast.makeText(MainActivity.this, "El usuario ya tiene un username asignado", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(MainActivity.this, "Error al comprobar el nombre de usuario", Toast.LENGTH_SHORT).show();
            }
        });
        return true;
    }

    private void initializeWiFiP2P() {
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        startPeerDiscovery();
    }
}