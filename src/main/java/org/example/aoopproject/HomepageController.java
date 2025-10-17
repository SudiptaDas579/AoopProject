package org.example.aoopproject;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Objects;


public class HomepageController {

    @FXML private GridPane calendarGrid;
    @FXML private Label monthLabel;
    @FXML private VBox weatherVBox;
    @FXML private Label cityLabel;
    @FXML private Label tempLabel;
    //@FXML private Label descLabel;
    //@FXML private Label extraLabel;
    @FXML private ImageView weatherImg;
    @FXML public Pane sceneView;

    public Stage stage;
    public Scene scene;

    @FXML
    public AnchorPane HomePagePane;


    private YearMonth currentMonth;

    private final String KEY = "73757b7eff9dec4fad51fca5465b14cd";


    @FXML
    public void initialize() {
        setBG();
//        currentMonth = YearMonth.now();
//        drawCalendar();
//        loadWeather("Dhaka");
        //updateBackgroundBasedOnTime();
    }

    public void setBG(){
        Image view = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/pictures/Company.png")));
        BackgroundImage viewBG = new BackgroundImage(
                view,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
        );
        HomePagePane.setBackground(new Background(viewBG));
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
            //String desc = json.getAsJsonArray("weather").get(0).getAsJsonObject().get("description").getAsString();
            String icon = json.getAsJsonArray("weather").get(0).getAsJsonObject().get("icon").getAsString();
            //int clouds = json.getAsJsonObject("clouds").get("all").getAsInt();

            cityLabel.setText(cityName);
            tempLabel.setText(temp + "°C");


            String iconUrl = "http://openweathermap.org/img/wn/" + icon + "@2x.png";
            weatherImg.setImage(new Image(iconUrl));

        } catch (Exception e) {
            e.printStackTrace();
            cityLabel.setText("Error loading weather");
        }
    }



//    public void updateBackgroundBasedOnTime() {
//        LocalTime now = LocalTime.now();
//        int hour = now.getHour();
//
//        BackgroundFill backgroundFill;
//
//        if (hour >= 6 && hour < 18) {
//            // Daytime
//            backgroundFill = new BackgroundFill(Color.LIGHTSKYBLUE, CornerRadii.EMPTY, Insets.EMPTY);
//        } else {
//            // Nighttime
//            backgroundFill = new BackgroundFill(Color.DARKSLATEBLUE, CornerRadii.EMPTY, Insets.EMPTY);
//        }
//
//        weatherVBox.setBackground(new Background(backgroundFill));
//    }

    private void drawCalendar() {
        calendarGrid.getChildren().clear();
        monthLabel.setText(currentMonth.getMonth() + " " + currentMonth.getYear());

        // Day headers
        String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (int i = 0; i < days.length; i++) {
            calendarGrid.add(new Label(days[i]), i, 0);
        }

        LocalDate firstDay = currentMonth.atDay(1);
        int dayOfWeek = firstDay.getDayOfWeek().getValue() % 7; // Sunday=0

        int daysInMonth = currentMonth.lengthOfMonth();
        int row = 1, col = dayOfWeek;
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = currentMonth.atDay(day);
            StackPane cell = new StackPane(new Label(String.valueOf(day)));
            cell.setPrefSize(80, 80);


            calendarGrid.add(cell, col, row);
            col++;
            if (col > 6) {
                col = 0;
                row++;
            }
        }
    }

    @FXML
    private void prevMonth() {
        currentMonth = currentMonth.minusMonths(1);
        drawCalendar();
    }

    @FXML
    private void nextMonth() {
        currentMonth = currentMonth.plusMonths(1);
        drawCalendar();
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

        final int[] seconds = {10};
        Timeline timeline = new Timeline();

        KeyFrame keyFrame = new KeyFrame(Duration.seconds(1), event -> {
            seconds[0]--;
            yesBtn.setText("Yes, I am (" + seconds[0] + ")");
            if (seconds[0] <= 0) {
                timeline.stop();
                stageDanger.close();
                openWorkInProgress();
            }
        });

        timeline.getKeyFrames().add(keyFrame);
        timeline.setCycleCount(10);
        timeline.play();

        yesBtn.setOnAction(e -> {
            timeline.stop();
            stageDanger.close();
            openWorkInProgress();
        });

        noBtn.setOnAction(e -> {
            timeline.stop();
            stageDanger.close();
            openAreYouSureWindow();
        });

        VBox layout = new VBox(15, label, yesBtn, noBtn);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        stageDanger.setScene(new Scene(layout, 300, 150));
        stageDanger.show();
    }

    private void openWorkInProgress() {
        Stage stage5 = new Stage();
        stage5.setTitle("Work in Progress");
        stage5.initModality(Modality.APPLICATION_MODAL);

        Label label = new Label("Work in Progress!!!");
        VBox layout = new VBox(20, label);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        stage5.setScene(new Scene(layout, 250, 120));
        stage5.show();
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

    }


    @FXML
    public void newsPortalSwitch(ActionEvent event) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("NewsPortal.fxml"));
        stage =(Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);

    }

    @FXML
    public void mapSwitch(ActionEvent event) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MapView.fxml"));
        stage =(Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);

    }
    @FXML
    public void eventHolidaySwitch(ActionEvent event) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("homepage.fxml"));
        stage =(Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);

    }
    @FXML
    public void logOut(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
        stage =(Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);

    }

}
