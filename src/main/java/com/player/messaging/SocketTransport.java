package com.player.messaging;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Socket-based message transport for inter-process communication.
 *
 * Responsibilities:
 * - Deliver messages between different JVM processes using TCP sockets
 * - Act as either server (listening) or client (connecting)
 * - Serialize/deserialize messages
 * - Manage socket connections and I/O streams
 * - Handle connection lifecycle and reconnection
 */
public class SocketTransport implements MessageTransport {
    private final int port;
    private final String host;
    private final boolean isServer;

    private MessageHandler handler;
    private ServerSocket serverSocket;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Thread listenerThread;
    private volatile boolean running = false;

    public SocketTransport(int port, boolean isServer) {
        this(port, "localhost", isServer);
    }

    public SocketTransport(int port, String host, boolean isServer) {
        this.port = port;
        this.host = host;
        this.isServer = isServer;
    }

    @Override
    public void setMessageHandler(MessageHandler handler) {
        this.handler = handler;
    }

    @Override
    public void start() {
        running = true;

        if (isServer) {
            startServer();
        } else {
            startClient();
        }

        startListener();
    }

    private void startServer() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server listening on port " + port);
            socket = serverSocket.accept();
            System.out.println("Client connected");
            initStreams();
        } catch (IOException e) {
            throw new RuntimeException("Failed to start server", e);
        }
    }

    private void startClient() {
        int maxRetries = 10;
        for (int i = 0; i < maxRetries; i++) {
            try {
                Thread.sleep(500);
                socket = new Socket(host, port);
                System.out.println("Connected to server at " + host + ":" + port);
                initStreams();
                return;
            } catch (IOException | InterruptedException e) {
                if (i == maxRetries - 1) {
                    throw new RuntimeException("Failed to connect to server", e);
                }
            }
        }
    }

    private void initStreams() throws IOException {
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());
    }

    private void startListener() {
        listenerThread = new Thread(() -> {
            while (running) {
                try {
                    Message message = (Message) in.readObject();
                    if (handler != null) {
                        handler.onMessageReceived(message);
                    }
                } catch (IOException | ClassNotFoundException e) {
                    if (running) {
                        System.err.println("Error reading message: " + e.getMessage());
                    }
                    break;
                }
            }
        });
        listenerThread.start();
    }

    @Override
    public void send(Message message) {
        try {
            if (out != null) {
                out.writeObject(message);
                out.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to send message", e);
        }
    }

    @Override
    public void stop() {
        running = false;

        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            System.err.println("Error closing connections: " + e.getMessage());
        }

        if (listenerThread != null) {
            try {
                listenerThread.join(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}