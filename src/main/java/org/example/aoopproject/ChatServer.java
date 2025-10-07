package org.example.aoopproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;

public class ChatServer implements Runnable {
    private final int port;
    private final Set<Socket> clients = Collections.synchronizedSet(new HashSet<>());

    public ChatServer(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            try (var pool = Executors.newCachedThreadPool()) {
                while (true) {
                    Socket client = serverSocket.accept();
                    clients.add(client);
                    pool.execute(new ClientHandler(client, clients));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientHandler implements Runnable {
    private final Socket client;
    private final Set<Socket> clients;

    public ClientHandler(Socket client, Set<Socket> clients) {
        this.client = client;
        this.clients = clients;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String msg = in.readLine();
            if (msg != null) {
                synchronized (clients) {
                    for (Socket s : clients) {
                        try {
                            PrintWriter out = new PrintWriter(s.getOutputStream(), true);
                            out.println(msg);
                        } catch (Exception ignored) {}
                    }
                }
            }
            client.close();
        } catch (IOException ignored) {}
    }
}
