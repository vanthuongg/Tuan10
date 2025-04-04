package com.example.bttuan10_socket.server;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean isRunning = false;
    private static final int port = 8080;

    public void startServer() {
        try {
            serverSocket = new ServerSocket(port);
            isRunning = true;
            Log.d("SocketServer", "Server started on port: " + port);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    waitForConnection();
                }
            }).start();
        } catch (IOException e) {
            Log.e("SocketServer", "Error starting server: " + e.getMessage());
        }
    }

    private void waitForConnection() {
        try {
            while (isRunning) {
                Log.d("SocketServer", "Waiting for connection...");
                clientSocket = serverSocket.accept();
                Log.d("SocketServer", "Client connected: " + clientSocket.getInetAddress());

                // Khởi tạo input và output streams
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                // Xử lý client kết nối
                handleClient();
            }
        } catch (IOException e) {
            Log.e("SocketServer", "Error accepting connection: " + e.getMessage());
        }
    }

    private void handleClient() {
        try {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                Log.d("SocketServer", "Message received: " + inputLine);

                // Xử lý thông điệp từ client
                String response = processMessage(inputLine);

                // Gửi phản hồi về client
                sendMessage(response);
            }
        } catch (IOException e) {
            Log.e("SocketServer", "Error handling client: " + e.getMessage());
        } finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                Log.e("SocketServer", "Error closing resources: " + e.getMessage());
            }
        }
    }

    private String processMessage(String message) {
        // Trả về một thông điệp với nội dung đã nhận
        return "Server received: " + message;
    }

    public void sendMessage(String message) {
        if (clientSocket != null && clientSocket.isConnected() && out != null) {
            out.println(message);
            Log.d("SocketServer", "Message sent: " + message);
        } else {
            Log.e("SocketServer", "Unable to send message. Socket not connected or output stream is null.");
        }
    }

    public void stopServer() {
        isRunning = false;
        try {
            if (serverSocket != null) {
                serverSocket.close();
                Log.d("SocketServer", "Server stopped");
            }
            if (clientSocket != null) {
                clientSocket.close();
            }
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            Log.e("SocketServer", "Error stopping server: " + e.getMessage());
        }
    }
}
