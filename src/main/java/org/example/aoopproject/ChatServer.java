package org.example.aoopproject;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class ChatServer extends Application {

    private static final int PORT = 5000;
    private final Set<ClientHandler> connectedClients = ConcurrentHashMap.newKeySet();

    @Override
    public void start(Stage primaryStage) {
        System.out.println("Server running on port " + PORT);
        startServer();
    }

    private void startServer() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                while (true) {
                    Socket user = serverSocket.accept();
                    Platform.runLater(() -> showAcceptDialog(user));
                }
            } catch (IOException e) {
                Platform.runLater(() -> showError("Server Error", e.getMessage()));
            }
        }).start();
    }


    private void showAcceptDialog(Socket user) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Chat Request");

        Label label = new Label("User connected\n");
        Button acceptBtn = new Button("Accept");
        Button rejectBtn = new Button("Reject");

        acceptBtn.setOnAction(e -> {
            dialog.close();
            try {
                ClientHandler handler = new ClientHandler(user);
                connectedClients.add(handler);
                new Thread(handler).start();
                openChatWindow(handler);
            } catch (IOException ex) {
                throw new RuntimeException();
            }
        });

        rejectBtn.setOnAction(e -> {
            dialog.close();
            try {
                PrintWriter out = new PrintWriter(user.getOutputStream(), true);
                out.println("Chat request rejected by admin.");
                user.close();
            } catch (IOException ignored) {}
        });

        VBox layout = new VBox(15, label, acceptBtn, rejectBtn);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        dialog.setScene(new Scene(layout, 280, 180));
        dialog.show();
    }




    private void openChatWindow(ClientHandler handler) {
        Stage chatStage = new Stage();
        chatStage.setTitle("Admin");
        chatStage.setX(200 + connectedClients.size() * 30);
        chatStage.setY(150 + connectedClients.size() * 30);

        TextArea chatBox = new TextArea();
        chatBox.setEditable(false);
        chatBox.setPrefHeight(250);

        TextField input = new TextField();
        input.setPromptText("Type your message...");
        Button sendBtn = new Button("Send");

        sendBtn.setOnAction(e -> {
            String msg = input.getText().trim();
            if (!msg.isEmpty()) {
                handler.sendMessage("Admin: " + msg);
                chatBox.appendText("Admin: " + msg + "\n");
                input.clear();
            }
        });

        VBox layout = new VBox(10, chatBox, input, sendBtn);
        layout.setStyle("-fx-padding: 10; -fx-alignment: center;");

        chatStage.setScene(new Scene(layout, 400, 350));
        chatStage.show();

        handler.setOnMessageReceived(message ->
                Platform.runLater(() -> chatBox.appendText(message + "\n")));

        handler.setOnDisconnect(() -> Platform.runLater(() -> {
            chatBox.appendText("User disconnected.\n");
            chatStage.setTitle("Chat (Disconnected) - " + handler.getClientIP());
        }));
    }

    private void showError(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    class ClientHandler implements Runnable {
        private final Socket user;
        private final BufferedReader in;
        private final PrintWriter out;
        private Consumer<String> messageCallback;
        private Runnable disconnectCallback;
        private volatile boolean connected = true;

        ClientHandler(Socket user) throws IOException {
            this.user = user;
            this.in = new BufferedReader(new InputStreamReader(user.getInputStream()));
            this.out = new PrintWriter(user.getOutputStream(), true);
        }

        public String getClientIP() {
            return user.getInetAddress().getHostAddress();
        }

        public void sendMessage(String msg) {
            out.println(msg);
        }

        public void setOnMessageReceived(Consumer<String> callback) {
            this.messageCallback = callback;
        }

        public void setOnDisconnect(Runnable callback) {
            this.disconnectCallback = callback;
        }

        @Override
        public void run() {
            try {
                String msg;
                while (connected && (msg = in.readLine()) != null) {
                    if (messageCallback != null)
                        messageCallback.accept(msg);
                }
            } catch (IOException ignored) {
            } finally {
                disconnect();
            }
        }

        private void disconnect() {
            connected = false;
            connectedClients.remove(this);
            try {
                user.close();
            } catch (IOException ignored) {}
            if (disconnectCallback != null)
                disconnectCallback.run();
        }
    }

    public void stop() throws Exception {
        for (ClientHandler handler : connectedClients) {
            try {
                handler.sendMessage("Server shutting down...");
                handler.disconnect();
            } catch (Exception ignored) {}
        }
        super.stop();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
