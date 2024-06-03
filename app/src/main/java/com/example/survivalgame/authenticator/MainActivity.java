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

import android.widget.Button;
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
import com.example.survivalgame.multiplayer.WiFiDirectCommunication;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import android.os.Handler;


@SuppressWarnings("deprecation")
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
    private Handler mHandler = new Handler();
    private Runnable mBroadcastRunnable;
    private static final int BROADCAST_INTERVAL = 1000; // 1s
    private static final int BROADCAST_DURATION = 5000; // 5s

    private boolean isSearchingForRoom = false;

    private FirebaseAuth firebaseAuth;
    private Button bLogOut, bStartSinglePlayer, bStartMultiplayer, bLeaderboards;
    private TextView tvUsername, tvTeammate;
    private FirebaseUser user;

    // P2P fields
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private BroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;

    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        initialize();
        setOnClickListeners();

        if (allPermissionsGranted()) {
            initializeWiFiP2P();
            startRoomSearch();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseAuth.getInstance().signOut();
    }

    private void startRoomSearch() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isSearchingForRoom) {
                    stopRoomSearch();
                    // Aquí puedes iniciar como el host
                    startHosting();
                } else {
                    // Envía el broadcast de búsqueda de sala
                    sendRoomBroadcast();
                    // Continúa la búsqueda hasta que se alcance la duración total
                    mHandler.postDelayed(this, BROADCAST_INTERVAL);
                }
            }
        }, BROADCAST_DURATION);
        isSearchingForRoom = true;
    }

    private void stopRoomSearch() {
        mHandler.removeCallbacksAndMessages(null);
        isSearchingForRoom = false;
    }

    private void sendRoomBroadcast() {
        // Aquí puedes enviar el broadcast de búsqueda de sala
        // Por ejemplo:
        // mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() { ... });
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
        new WiFiDirectCommunication.ServerAsyncTask(new WiFiDirectCommunication.OnDataReceivedListener() {
            @Override
            public void onDataReceived(String data) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Received: " + data, Toast.LENGTH_LONG).show());
            }
        }).execute();
    }

    @SuppressLint("MissingPermission")
    public void handleConnectionInfo(WifiP2pInfo info) {
        if (info.groupFormed && info.isGroupOwner) {
            // Start server task
            new WiFiDirectCommunication.ServerAsyncTask(new WiFiDirectCommunication.OnDataReceivedListener() {
                @Override
                public void onDataReceived(String data) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Received: " + data, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }).execute();
        } else if (info.groupFormed) {
            // Start client task
            new WiFiDirectCommunication.ClientAsyncTask(info.groupOwnerAddress.getHostAddress()).execute("Hello from client!");
        }
    }

    @SuppressLint("MissingPermission")
    private void startPeerDiscovery() {
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                // Peer discovery started
                Toast.makeText(MainActivity.this, "Peer discovery started", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reasonCode) {
                // Peer discovery failed
                // Peer discovery failed
                String reasonMsg;
                switch (reasonCode) {
                    case WifiP2pManager.P2P_UNSUPPORTED:
                        reasonMsg = "P2P is unsupported on this device.";
                        break;
                    case WifiP2pManager.ERROR:
                        reasonMsg = "Internal error occurred.";
                        break;
                    case WifiP2pManager.BUSY:
                        reasonMsg = "Framework is busy and unable to service this request.";
                        break;
                    default:
                        reasonMsg = "Peer discovery failed with reason code: " + reasonCode;
                        break;
                }
                Toast.makeText(MainActivity.this, reasonMsg, Toast.LENGTH_SHORT).show();
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

    private void initialize() {
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
