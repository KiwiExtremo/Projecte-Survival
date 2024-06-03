package com.example.survivalgame.multiplayer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.example.survivalgame.authenticator.MainActivity;

import java.util.Collection;

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {
    private final WifiP2pManager mManager;
    private final WifiP2pManager.Channel mChannel;
    private final MainActivity mActivity;

    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, MainActivity activity) {
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wi-Fi P2P is enabled
            } else {
                // Wi-Fi P2P is not enabled
                Toast.makeText(context, "Wi-Fi P2P is not enabled", Toast.LENGTH_SHORT).show();
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            if (mManager != null) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mManager.requestPeers(mChannel, new WifiP2pManager.PeerListListener() {
                        @Override
                        public void onPeersAvailable(WifiP2pDeviceList peerList) {
                            Collection<WifiP2pDevice> devices = peerList.getDeviceList();
                            if (devices.isEmpty()) {
                                // No peers found, start hosting
                                mActivity.startHosting();
                            } else {
                                // Peers found, connect to a peer
                                mActivity.connectToPeer(devices.iterator().next());
                            }
                        }
                    });
                } else {
                    // Handle the case where the required permission is not granted
                    Toast.makeText(context, "Location permission is required for peer discovery", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            if (mManager == null) {
                return;
            }

            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {
                // We are connected with the other device
                mManager.requestConnectionInfo(mChannel, new WifiP2pManager.ConnectionInfoListener() {
                    @Override
                    public void onConnectionInfoAvailable(WifiP2pInfo info) {
                        // Connection information available
                        // Start communication with the other device
                        mActivity.handleConnectionInfo(info);
                    }
                });
            } else {
                // Disconnected from peer
                Toast.makeText(context, "Disconnected from peer", Toast.LENGTH_SHORT).show();
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
        }
    }
}
