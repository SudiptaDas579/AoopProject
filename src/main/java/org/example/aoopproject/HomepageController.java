package org.example.aoopproject;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;


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



    private YearMonth currentMonth;
    private final String KEY = "73757b7eff9dec4fad51fca5465b14cd";


    @FXML
    public void initialize() {
        currentMonth = YearMonth.now();
        drawCalendar();
        loadWeather("Dhaka");
        //updateBackgroundBasedOnTime();
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
}
