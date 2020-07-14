package com.artyomgeta;

public interface ServerListener {
    void onClientConnected(Client client);
    void onClientDisconnected(Client client);
    void onMessageReceived(Client client, String message);
}