package org.example.aoopproject;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.mail.MessagingException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;


public class HomepageController {

    @FXML private GridPane calendarGrid;
    @FXML private Label monthLabel;
    @FXML private Label cityLabel;
    @FXML private Label tempLabel;
    @FXML private ImageView weatherImg;
    @FXML public Pane sceneView;

    public Stage stage;
    public Scene scene;

    @FXML
    public AnchorPane HomePagePane;
    @FXML
    public ImageView dotView;


    private YearMonth currentMonth;

    private final String KEY = "73757b7eff9dec4fad51fca5465b14cd";
    @FXML
    private Pane upcomingEventsPane;

    public ContextMenu menu=new ContextMenu();

    @FXML
    public void initialize() {


        setBG();
        loadUpcomingEvents();

        loadWeather("Dhaka");
        OutButton();

        MenuItem item1 = new MenuItem("Language");
        MenuItem item2 = new MenuItem("Change Theme");
        MenuItem item3 = new MenuItem("log Out");

        menu.getItems().addAll(item1,item2,item3);

        item1.setOnAction(e -> {
           switchLanguage(e);

        });
        item2.setOnAction(e -> {
            switchTheme(e);
        });
        item3.setOnAction(event -> {
            try {
                logOut(event);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        dotView.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
            menu.hide();

            menu.show(HomePagePane, e.getScreenX()-150, e.getScreenY()+18);


       }
        });


    }

    public void setBG(){
        Image view = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/pictures/lightHome.png")));
        BackgroundImage viewBG = new BackgroundImage(
                view,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
        );
        HomePagePane.setBackground(new Background(viewBG));
    }

    public void OutButton(){

        Image view = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/pictures/dots.png")));
        dotView.setImage(view);
    }


    public void switchLanguage(ActionEvent e){

    }
    public void switchTheme(ActionEvent e){
        if(scene == null){
            scene = HomePagePane.getScene(); // fallback
        }
        ThemeManager.setScene(scene);
        ThemeManager.toggleTheme();
    }


    @FXML private void newsBtn(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader();
    }

    private void loadWeather(String city) {
        try {
            String urlStr = "https://api.openweathermap.org/data/2.5/weather?q="
                    + city + "&appid=" + KEY + "&units=metric";
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            JsonObject json = JsonParser.parseReader(new InputStreamReader(conn.getInputStream())).getAsJsonObject();

            String cityName = json.get("name").getAsString();
            double temp = json.getAsJsonObject("main").get("temp").getAsDouble();
            String icon = json.getAsJsonArray("weather").get(0).getAsJsonObject().get("icon").getAsString();


            cityLabel.setText(cityName);
            tempLabel.setText(temp + "°C");


            String iconUrl = "http://openweathermap.org/img/wn/" + icon + "@2x.png";
            weatherImg.setImage(new Image(iconUrl));

        } catch (Exception e) {
            e.printStackTrace();
            cityLabel.setText("Error loading weather");
        }

    }


    //file read from events.txt
    private void loadUpcomingEvents() {
        upcomingEventsPane.getChildren().clear();
        Map<LocalDate, List<String>> events = new HashMap<>();

        File eventFile = new File("events.txt");
        if (!eventFile.exists()) {
            Label noEventLabel = new Label("No upcoming events.");
            noEventLabel.setLayoutX(10);
            noEventLabel.setLayoutY(10);
            upcomingEventsPane.getChildren().add(noEventLabel);
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(eventFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", 2);
                if (parts.length == 2) {
                    LocalDate date = LocalDate.parse(parts[0]);
                    String event = parts[1];
                    events.computeIfAbsent(date, k -> new ArrayList<>()).add(event);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Filter and sort events
        LocalDate today = LocalDate.now();
        List<Map.Entry<LocalDate, List<String>>> upcoming = events.entrySet().stream()
                .filter(e -> !e.getKey().isBefore(today))
                .sorted(Map.Entry.comparingByKey())
                .limit(5)
                .toList();

        double y = 10;
        for (Map.Entry<LocalDate, List<String>> entry : upcoming) {
            for (String event : entry.getValue()) {
                Label label = new Label(entry.getKey() + ": " + event);
                label.setLayoutX(10);
                label.setLayoutY(y);
                upcomingEventsPane.getChildren().add(label);
                y += 25; // spacing between events
            }
        }

        if (upcomingEventsPane.getChildren().isEmpty()) {
            Label noEventLabel = new Label("No upcoming events.");
            noEventLabel.setLayoutX(10);
            noEventLabel.setLayoutY(10);
            upcomingEventsPane.getChildren().add(noEventLabel);
        }
    }







    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private static final String IP = "localhost";  //jeba 192.168.0.100
    private static final int PORT = 5000;

    @FXML
    public void handleEmergency() {
        openDangerWindow();
    }

    private void openDangerWindow() {
        Stage stageDanger = new Stage();
        stageDanger.setTitle("Emergency!!");
        stageDanger.initModality(Modality.APPLICATION_MODAL);

        Label label = new Label("Are you in danger?");
        Button yesBtn = new Button("Yes, I am (10)");
        Button noBtn = new Button("No");

        yesBtn.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

        final Timeline[] timeline = new Timeline[1];
        final int[] seconds = {10};

        timeline[0] = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> {
                    seconds[0]--;
                    yesBtn.setText("Yes, I am (" + seconds[0] + ")");
                    if (seconds[0] <= 0) {
                        timeline[0].stop();  // safe reference
                        stageDanger.close();
                        triggerEmergencyAlert();
                    }
                })
        );
        timeline[0].setCycleCount(10);
        timeline[0].play();

        yesBtn.setOnAction(e -> {
            timeline[0].stop();  // safe reference
            stageDanger.close();
            triggerEmergencyAlert();
        });

        noBtn.setOnAction(e -> {
            timeline[0].stop();
            stageDanger.close();
            openAreYouSureWindow();
        });

        VBox layout = new VBox(15, label, yesBtn, noBtn);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        stageDanger.setScene(new Scene(layout, 300, 150));
        stageDanger.show();
    }
    private void triggerEmergencyAlert() {
        new Thread(() -> {
            try {
                Mail.sendEmergencyAlert(
                        "graceepidice@gmail.com",
                        "EMERGENCY! User pressed the emergency button.\n" +
                                "Location: Dhaka, Bangladesh.\n"
                );

                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Rescue Alert Sent");
                    alert.setHeaderText("Help is on the way!");
                    alert.setContentText("An emergency alert has been sent to rescue authorities.");
                    alert.show();
                });

                notifyAdminServer("Emergency Triggered! User pressed YES button.");

            } catch (MessagingException ex) {
                ex.printStackTrace();
                Platform.runLater(() -> {
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setTitle("Email Error");
                    error.setHeaderText("Failed to send emergency alert!");
                    error.setContentText("Please check your internet connection or email settings.");
                    error.show();
                });
            }
        }).start();
    }

    private void notifyAdminServer(String message) {
        new Thread(() -> {
            try (Socket socket = new Socket(IP, PORT);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                out.println(message);
            } catch (IOException ex) {
                System.err.println("Failed to contact server: " + ex.getMessage());
            }
        }).start();
    }


    private void startChat() {
        openChat("User", 580);
    }

    private void openAreYouSureWindow() {
        Stage stage0 = new Stage();
        stage0.setTitle("Confirmation");
        stage0.initModality(Modality.APPLICATION_MODAL);

        Label label = new Label("Are you sure?");
        Button closeBtn = new Button("Close");
        Button chatBtn = new Button("Chat with Emergency Authority");

        closeBtn.setOnAction(e -> stage0.close());

        chatBtn.setOnAction(e -> {
            stage0.close();
            startChat();
        });

        VBox layout = new VBox(15, label, closeBtn, chatBtn);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        stage0.setScene(new Scene(layout, 300, 150));
        stage0.show();
    }

    private void openChat(String title, int x) {
        Stage stage1 = new Stage();
        stage1.setTitle(title);
        stage1.setX(x);
        stage1.setY(300);

        TextArea chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setPrefHeight(170);

        TextField input = new TextField();
        input.setPromptText("Type your message...");
        Button sendBtn = new Button("Send");

        VBox layout = new VBox(10, chatArea, input, sendBtn);
        layout.setStyle("-fx-padding: 10; -fx-alignment: center;");
        stage1.setScene(new Scene(layout, 350, 250));
        stage1.show();

        new Thread(() -> {
            try {
                Socket socket = new Socket(IP, PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                sendBtn.setOnAction(e -> {
                    String msg = input.getText().trim();
                    if (!msg.isEmpty()) {
                        out.println(title + ": " + msg);
                        chatArea.appendText("Me: " + msg + "\n");
                        input.clear();
                    }
                });

                String message;
                while ((message = in.readLine()) != null) {
                    String finalMsg = message;
                    javafx.application.Platform.runLater(() -> {
                        chatArea.appendText(finalMsg + "\n");
                    });
                }

                socket.close();
            } catch (IOException e) {
                javafx.application.Platform.runLater(() ->
                        chatArea.appendText("Unable to connect Server.\n"));
            }
        }).start();
    }

    @FXML
    public void homePageSwitch(ActionEvent event) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("homepage.fxml"));
        stage =(Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);

        ThemeManager.setScene(scene);

    }


    @FXML
    public void newsPortalSwitch(ActionEvent event) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("NewsPortal.fxml"));
        stage =(Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        ThemeManager.setScene(scene);

    }

    @FXML
    public void mapSwitch(ActionEvent event) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MapView.fxml"));
        stage =(Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        ThemeManager.setScene(scene);

    }
    @FXML

    public void eventHolidaySwitch(ActionEvent event) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("EventsHolidays.fxml"));
        stage =(Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        ThemeManager.setScene(scene);


    }
    @FXML
    public void logOut(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
        stage =(Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);


    }

}
