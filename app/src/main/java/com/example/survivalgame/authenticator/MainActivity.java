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
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.survivalgame.GameActivity;
import com.example.survivalgame.LeaderboardAdapter;
import com.example.survivalgame.PreferencesActivity;
import com.example.survivalgame.R;
import com.example.survivalgame.multiplayer.WiFiDirectBroadcastReceiver;
import com.example.survivalgame.multiplayer.WiFiDirectCommunication;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


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
    private static final int NO_ERROR = 0;
    private static final int USERNAME_ALREADY_EXISTS = 1;
    private FirebaseAuth firebaseAuth;
    private Button bLogOut, bStartSinglePlayer, bStartMultiplayer, bLeaderboards;
    private TextView tvUsername, tvTeammate;
    private FirebaseUser user;
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private BroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private boolean isUsernameNull = false;
    private boolean isUsernameChecked = false;
    private int usernameError;
    private boolean isMusic;
    private MediaPlayer mp;
    public static SharedPreferences pref;
    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        initialize();
        getFromSharedPrefs();
        updateBackgroundMusic();
        setOnClickListeners();

        checkUserLoggedIn();

        if (allPermissionsGranted()) {
            initializeWiFiP2P();
            startRoomSearch();
        } else {
            requestPermissions();
        }
    }

    private void checkUserLoggedIn() {
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
            startActivity(intent);
            finish();

        } else {
            String email = user.getEmail();
            checkUsername(email, () -> {
                if (isUsernameNull) {
                    showDialogNewUser(user.getEmail());
                } else {
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                DataSnapshot usernameSnapshot = snapshot.child(email.replace(".", "_")).child("username");
                                if (usernameSnapshot.exists() && usernameSnapshot.getValue() != null) {
                                    String data = usernameSnapshot.getValue().toString();
                                    tvUsername.setText(data);
                                } else {
                                    showDialogNewUser(user.getEmail());
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            });
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
        FirebaseAuth.getInstance().signOut();

        if (mp != null && mp.isPlaying()) {
            mp.pause();
        }
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
                Toast.makeText(this, getString(R.string.toast_lobby_error_no_permissions), Toast.LENGTH_LONG).show();
                tvTeammate.setText(getString(R.string.text_view_error_no_permissions));
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
                Toast.makeText(MainActivity.this, getString(R.string.toast_lobby_multiplayer_discovery_started), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reasonCode) {
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
        intent.putExtra("userEmail", user.getEmail());

        startActivity(intent);
    }

    private void bOnClickStartMultiPlayer() {
        Intent intent = new Intent(getApplicationContext(), GameActivity.class);
        intent.putExtra("Mode", MODE_MULTIPLAYER);
        intent.putExtra("userEmail", user.getEmail());
        // TODO put extra boolean isHost
        startActivity(intent);
    }

    private void bOnClickShowLeaderboards() {
        getScoresFromFirebase();
    }

    private void getScoresFromFirebase() {
        databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();
                    if (snapshot.exists()) {
                        Map<String, Integer> scoreMap = new HashMap<>();

                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            String username = userSnapshot.child("username").getValue(String.class);
                            Integer score = userSnapshot.child("puntuacion").getValue(Integer.class);

                            if (username != null && score != null) {
                                scoreMap.put(username, score);
                            }
                        }
                        showDialogLeaderboards(scoreMap);
                    } else {
                        Toast.makeText(MainActivity.this, getString(R.string.toast_lobby_error_no_leaderboards_data), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.toast_lobby_error_database), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private Map<String, String> sortMapByValue(Map<String, Integer> unsortedMap) {
        List<Map.Entry<String, Integer>> list = new LinkedList<>(unsortedMap.entrySet());

        Collections.sort(list, (o1, o2) -> (o2.getValue()).compareTo(o1.getValue()));

        Map<String, String> sortedMap = new LinkedHashMap<>();

        for (Map.Entry<String, Integer> entry : list) {
            sortedMap.put(entry.getKey(), getString(R.string.hash_map_leaderboards_score, entry.getValue()));
        }
        return sortedMap;
    }

    private void setViewMaxSize(LeaderboardAdapter lvScores, ListView scores) {
        // set max size if there are more than 5 items inside the listView
        if (lvScores.getCount() > 5) {
            // grab the size of any given item
            View item = lvScores.getView(0, null, scores);
            item.measure(0, 0);

            // set the size of the listView to 5.7 times the item size
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(0, (int) (5.7 * item.getMeasuredHeight()));
            scores.setLayoutParams(params);
        }
    }

    private void showDialogLeaderboards(Map<String, Integer> scoreMap) {
        // Inflate the leaderboards xml
        View vLeaderboards = getLayoutInflater().inflate(R.layout.activity_leaderboard, null);

        // Get leaderboards' data from database
        Map<String, String> mPlayerScores = sortMapByValue(scoreMap);

        LeaderboardAdapter scoresAdapter = new LeaderboardAdapter(mPlayerScores);

        ListView lvScores = vLeaderboards.findViewById(R.id.lvScores);
        lvScores.setAdapter(scoresAdapter);

        // Setup the dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.dialog_leaderboards_title));
        builder.setMessage(getString(R.string.dialog_leaderboards_message));

        // set up the max size of the view containing the listView
        setViewMaxSize(scoresAdapter, lvScores);
        builder.setView(vLeaderboards);

        // add the buttons
        builder.setPositiveButton(getString(R.string.dialog_leaderboards_positive), (dialog, which) -> {
            // Do nothing, just close dialog box
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
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

    private void showDialogNewUser(String email) {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.dialog_new_user_title));
        builder.setMessage(getString(R.string.dialog_new_user_body));

        EditText etUsername = new EditText(this);
        etUsername.setSingleLine();
        etUsername.setHint(getString(R.string.dialog_new_user_hint));

        LinearLayout llUsername = createWrappedLayout(etUsername);
        builder.setView(llUsername);

        builder.setPositiveButton(getString(R.string.dialog_pause_positive), (dialog, which) -> {
        });


        builder.setPositiveButton(getString(R.string.dialog_new_user_positive), (dialog, which) -> {
            // Do nothing, since we override the onClickListener later
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCancelable(false);

        // Override onClickListener to avoid auto-dismissing
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String username = etUsername.getText().toString();
            if ("".equals(username)) {
                etUsername.setError(getString(R.string.dialog_new_user_edit_text_error_empty));
            } else {
                checkAndSetUsername(email, username);

                if (usernameError == NO_ERROR) {
                    dialog.dismiss();

                } else if (usernameError == USERNAME_ALREADY_EXISTS) {
                    etUsername.setError(getString(R.string.dialog_new_user_edit_text_error_already_in_use));
                }
            }
        });
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
        pref = getSharedPreferences(getPackageName() + "_preferences", MODE_PRIVATE);

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
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
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
                    Toast.makeText(MainActivity.this, getString(R.string.toast_lobby_new_user_saved), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, getString(R.string.toast_lobby_error_check_database), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUsername(String email, Runnable callback) {
        DatabaseReference nameReference = firebaseDatabase.getReference("Users").child(email.replace(".", "_")).child("username");

        nameReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot dataSnapshot = task.getResult();
                if (dataSnapshot.exists()) {
                    String currentUsername = dataSnapshot.getValue(String.class);
                    if ("<null>".equals(currentUsername)) {
                        isUsernameNull = true;
                    } else {
                        tvUsername.setText(currentUsername);
                    }
                }
            } else {
                Toast.makeText(MainActivity.this, getString(R.string.toast_lobby_error_check_database), Toast.LENGTH_SHORT).show();
            }
            callback.run();
        });
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