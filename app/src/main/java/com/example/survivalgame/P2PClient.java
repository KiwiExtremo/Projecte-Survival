package com.example.survivalgame;

import android.util.Log;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.Handshakedata;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;

public class P2PClient extends WebSocketClient {

    private static final String TAG = "P2PClient";

    public P2PClient(String serverUri) throws URISyntaxException {
        super(new URI(serverUri));
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.d(TAG, "Connected to server");
        send("check_room"); // Enviar solicitud para verificar si hay una sala disponible
    }

    @Override
    public void onMessage(String message) {
        Log.d(TAG, "Message from server: " + message);
        
        // Manejar mensajes para unirse o crear una sala
        if (message.equals("room_found")) {
            Log.d(TAG, "Room found, joining...");
        } else if (message.equals("no_room")) {
            Log.d(TAG, "No room found, creating room...");
        }
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

    public void searchRoom() {
        Log.d(TAG, "Search Room: Connected to server");
        send("check_room");
    }

    public void createRoom() {
        send("create_room");
        Log.d(TAG, "Create Room: Room created");
    }
}
