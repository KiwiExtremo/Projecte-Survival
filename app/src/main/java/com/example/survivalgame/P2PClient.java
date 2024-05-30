package com.example.survivalgame;
import android.util.Log;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class P2PClient extends WebSocketClient {

    private static final String TAG = "P2PClient";

    public P2PClient(String serverUri) throws URISyntaxException {
        super(new URI(serverUri));
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.d(TAG, "Connected to server");
    }

    @Override
    public void onMessage(String message) {
        Log.d(TAG, "Message from server: " + message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.d(TAG, "Disconnected from server");
    }

    @Override
    public void onError(Exception ex) {
        Log.e(TAG, "Error: " + ex.getMessage(), ex);
    }

    public void sendMessage(String message) {
        this.send(message);
    }
}
