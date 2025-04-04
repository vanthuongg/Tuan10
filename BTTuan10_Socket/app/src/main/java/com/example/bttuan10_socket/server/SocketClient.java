package com.example.bttuan10_socket.server;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public void connectToServer(String host, int port) {
        try {
            socket = new Socket(host, port);

            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            Log.d("SocketClient", "Connected to server: " + host + ":" + port);
        } catch (IOException e) {
            Log.e("SocketClient", "Error connecting to server: " + e.getMessage());
        }
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    public void sendMessage(String message) {
        if (socket != null && socket.isConnected() && out != null) {
            out.println(message);
            Log.d("SocketClient", "Message sent: " + message);
        } else {
            Log.e("SocketClient", "Cannot send message - socket not connected");
        }
    }

    public String receiveMessage() {
        try {
            if (socket != null && socket.isConnected() && in != null) {
                String message = in.readLine();
                Log.d("SocketClient", "Message received: " + message);
                return message;
            }
        } catch (IOException e) {
            Log.e("SocketClient", "Error receiving message: " + e.getMessage());
        }
        return null;
    }

    public void closeConnection() {
        try {
            if (socket != null) {
                out.close();
                in.close();
                socket.close();
                Log.d("SocketClient", "Connection closed");
            }
        } catch (IOException e) {
            Log.e("SocketClient", "Error closing connection: " + e.getMessage());
        }
    }
}
