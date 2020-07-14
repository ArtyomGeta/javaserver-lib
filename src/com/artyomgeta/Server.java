package com.artyomgeta;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("unused")
public class Server {
    private int port = 8080;
    private ServerSocket serverSocket;
    private PrintWriter printWriter;
    private final List<Client> list = new ArrayList<>();
    private boolean working = false;
    private int maximum = Integer.MAX_VALUE;
    private boolean throwsException = false;
    private String stopMessage = null;

    public final int getPort() {
        return port;
    }

    public final void setPort(int port) throws IllegalAccessException {
        if (port < 1024)
            throw new IllegalAccessException("All ports lover than 1024 are blocked.");
        this.port = port;
    }

    public Server() {
        initialize(port);
    }

    public List<Client> getClients() {
        return list;
    }

    private void initialize(int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void launch() {
        working = true;
        try {
            initialize(getPort());
            while (working) {
                    if (list.size() <= maximum) {
                        Socket socket = serverSocket.accept();
                        Client client = new Client(socket, this);
                        client.initialize(socket, this);
                    } else if (throwsException) {
                        throw new IndexOutOfBoundsException("Maximum users: " + maximum);
                    }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    private ServerListener listener = null;

    public void setServerListener(ServerListener listener) {
        this.listener = listener;
    }

    protected void onClientConnected(Client client) {
        if (this.listener != null) {
            this.listener.onClientConnected(client);
        }
    }

    public void setStopMessage(String message) {
        this.stopMessage = message;
    }

    public String getStopMessage() {
        return stopMessage;
    }

    protected void onClientDisconnected(Client client) {
        if (this.listener != null) {
            this.listener.onClientDisconnected(client);
        }
    }

    protected void onMessageReceived(Client client, String message) {
        if (this.listener != null) {
            this.listener.onMessageReceived(client, message);
        }
    }

    public final void stop() {
        working = false;
    }


    public void setMaximumClientsLength(int value) {
        maximum = value;
    }

    public void setMaximumClientsLength(int value, boolean throwsException) {
        this.maximum = value;
        this.throwsException = throwsException;
    }

    public int getMaximumClientsLength() {
        return maximum;
    }
}
