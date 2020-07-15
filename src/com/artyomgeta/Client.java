package com.artyomgeta;

import java.io.*;
import java.net.Socket;

@SuppressWarnings("unused")
public class Client {
    private Socket socket;
    private PrintWriter printWriter;
    private DataInputStream dataInputStream;
    private boolean isConnected = false;
    private BufferedReader bufferedReader;
    private Server server;

    Client(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    @SuppressWarnings("CopyConstructorMissesField")
    public Client(Client client) {
        this.socket = client.getSocket();
        this.server = client.getServer();
    }


    protected void initialize(Socket socket, Server server) {
        new Thread(() -> {
            try {
                printWriter = new PrintWriter(socket.getOutputStream(), true);
                dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                isConnected = true;
                server.getClients().add(this);
                server.onClientConnected(this);
                while (isConnected) {
                    try {
                        String message = bufferedReader.readLine();
                        //noinspection ConstantConditions
                        if (message.equals("null") || message == null)
                            break;
                        server.onMessageReceived(this, message);
                    } catch (NullPointerException exception) {
                        break;
                    }
                }
                disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    protected Server getServer() {
        return server;
    }

    public void print(String message) {
        printWriter.println(message);
    }

    public String requireAnswer(String question, boolean callOnMessageReceived) {
        if (question != null)
            print(question);
        String result = null;
        try {
            result = bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (callOnMessageReceived)
            server.onMessageReceived(this, result);
        return result;
    }

    public void disconnect() {
        server.onClientDisconnected(this);
        isConnected = false;
        server.getClients().remove(this);
        try {
            dataInputStream.close();
            printWriter.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public final Socket getSocket() {
        return socket;
    }

    public String getIp() {
        return socket.getInetAddress().getHostAddress();
    }

}
