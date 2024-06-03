package com.example.survivalgame.authenticator;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.example.survivalgame.GameActivity;
import com.example.survivalgame.PreferencesActivity;
import com.example.survivalgame.R;
import com.example.survivalgame.multiplayer.WiFiDirectBroadcastReceiver;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_CODE = 1;
    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private static final String[] REQUIRED_PERMISSIONS_API_33 = {
            Manifest.permission.NEARBY_WIFI_DEVICES
    };
    public static final int MODE_SINGLEPLAYER = 0;
    public static final int MODE_MULTIPLAYER = 1;
    private FirebaseAuth firebaseAuth;
    private Button bLogOut, bStartSinglePlayer, bStartMultiplayer, bLeaderboards;
    private TextView tvUsername, tvTeammate;
    private FirebaseUser user;
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private BroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;
    private boolean isMusic;
    private MediaPlayer mp;
    private SharedPreferences pref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        initialize();
        getFromSharedPrefs();
        updateBackgroundMusic();
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
        initializeWiFiP2P();
        registerReceiver(mReceiver, mIntentFilter);

        updateBackgroundMusic();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);

        if (mp != null && mp.isPlaying()) {
            mp.pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mp != null && mp.isPlaying()) {
            mp.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // unregisterReceiver(mReceiver);
        FirebaseAuth.getInstance().signOut();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.iSettings) {
            Intent i = new Intent(this, PreferencesActivity.class);
            startActivity(i);
        }
        if (item.getItemId() == R.id.iInfo) {
            showDialogInfo();
            return true;
        }
        // If we got here, the user's action was not recognized.
        // Invoke the superclass to handle it.
        return super.onOptionsItemSelected(item);
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // API 33
            ActivityCompat.requestPermissions(this,
                    concatArrays(REQUIRED_PERMISSIONS, REQUIRED_PERMISSIONS_API_33), PERMISSIONS_REQUEST_CODE);

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
                tvTeammate.setText("You can only play singleplayer");
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
        // TODO put extra boolean isHost
        startActivity(intent);
    }

    private void bOnClickShowLeaderboards() {
        // TODO fetch data from database and show it
        showDialogLeaderboards();
    }

    private void showDialogInfo() {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.dialog_information_title));
        builder.setMessage(getString(R.string.dialog_information_body));

        // add the buttons
        builder.setPositiveButton(getString(R.string.dialog_information_positive), (dialog, which) -> {
            // Do nothing, just close dialog box
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDialogNewUser() {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.dialog_pause_title));
        builder.setMessage(getString(R.string.dialog_pause_body));

        // add the buttons
        builder.setPositiveButton(getString(R.string.dialog_pause_positive), (dialog, which) -> {
            // TODO Check input username

            // TODO Save username to the database
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCancelable(false);
    }

    private void showDialogLeaderboards() {
        // TODO show leaderboards
    }

    private void updateBackgroundMusic() {
        isMusic = pref.getBoolean("check_lobby_music", true);

        if (isMusic) {
            mp.setLooping(true);
            mp.start();

        } else if (mp != null && mp.isPlaying()) {
            mp.pause();
        }
    }

    private void getFromSharedPrefs() {
        pref = PreferenceManager.getDefaultSharedPreferences(this);

        mp = MediaPlayer.create(MainActivity.this, R.raw.lobby_bg_music);
    }

    private void initialize() {
        Toolbar myToolBar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolBar);

        firebaseAuth = FirebaseAuth.getInstance();

        bLogOut = findViewById(R.id.bLogOut);
        bStartSinglePlayer = findViewById(R.id.bSinglePlayer);
        bStartMultiplayer = findViewById(R.id.bMultiPlayer);
        bLeaderboards = findViewById(R.id.bLeaderboards);
        tvUsername = findViewById(R.id.tvUsername);
        tvTeammate = findViewById(R.id.tvTeammate);
        user = firebaseAuth.getCurrentUser();

        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
            startActivity(intent);
            finish();

        } else {
            // TODO get username from database
            String username = "Thresholder";

            if ("<Null>".equals(username)) {
                showDialogNewUser();

            } else {
                tvUsername.setText(username);
            }
        }
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