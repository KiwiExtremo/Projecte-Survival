package com.example.survivalgame.multiplayer;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

@SuppressWarnings("deprecation")
public class WiFiDirectCommunication {

    private static final int PORT = 8888;

    public interface OnDataReceivedListener {
        void onDataReceived(String data);
    }

    public static class ServerAsyncTask extends AsyncTask<Void, Void, String> {

        private ServerSocket serverSocket;
        private OnDataReceivedListener listener;

        public ServerAsyncTask(OnDataReceivedListener listener) {
            this.listener = listener;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                serverSocket = new ServerSocket(PORT);
                Socket client = serverSocket.accept();
                InputStream inputStream = client.getInputStream();
                byte[] buffer = new byte[1024];
                int bytes;
                StringBuilder data = new StringBuilder();
                while ((bytes = inputStream.read(buffer)) != -1) {
                    data.append(new String(buffer, 0, bytes));
                }
                return data.toString();
            } catch (IOException e) {
                Log.e("ServerAsyncTask", "IOException: " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (listener != null && result != null) {
                listener.onDataReceived(result);
            }
        }
    }

    public static class ClientAsyncTask extends AsyncTask<String, Void, Void> {

        private String hostAddress;

        public ClientAsyncTask(String hostAddress) {
            this.hostAddress = hostAddress;
        }

        @Override
        protected Void doInBackground(String... params) {
            String data = params[0];
            try {
                Socket socket = new Socket();
                socket.bind(null);
                socket.connect(new InetSocketAddress(hostAddress, PORT), 5000);
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(data.getBytes());
                outputStream.close();
                socket.close();
            } catch (IOException e) {
                Log.e("ClientAsyncTask", "IOException: " + e.getMessage());
            }
            return null;
        }
    }
}
