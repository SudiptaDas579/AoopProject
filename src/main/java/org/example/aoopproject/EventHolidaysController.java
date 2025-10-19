package org.example.aoopproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

public class EventHolidaysController implements Initializable {

    @FXML
    private GridPane calendarGrid;
    @FXML
    private Label monthLabel;
    public Stage stage;
    public Scene scene;


    private YearMonth currentMonth;
    private HashMap<LocalDate, List<String>> events = new HashMap<>();
    private final File eventFile = new File("events.txt");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentMonth = YearMonth.now();
        loadEventsFromFile();
        drawCalendar();


    }


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
            StackPane cell = createDayCell(date);
            calendarGrid.add(cell, col, row);

            col++;
            if (col > 6) {
                col = 0;
                row++;
            }
        }
    }

    private StackPane createDayCell(LocalDate date) {
        Rectangle rect = new Rectangle(80, 80);
        rect.setFill(Color.WHITE);
        rect.setStroke(Color.LIGHTGRAY);

        VBox box = new VBox(2);
        box.setAlignment(javafx.geometry.Pos.TOP_CENTER);

        Label dayLabel = new Label(String.valueOf(date.getDayOfMonth()));
        dayLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        box.getChildren().add(dayLabel);

        // If events exist, display all
        if (events.containsKey(date)) {
            rect.setFill(Color.LIGHTBLUE);
            for (String ev : events.get(date)) {
                Label evLabel = new Label(ev);
                evLabel.setStyle("-fx-font-size: 10; -fx-text-fill: darkblue;");
                box.getChildren().add(evLabel);
            }
        }

        StackPane cell = new StackPane(rect, box);
        cell.setOnMouseClicked(e -> showEventDialog(date));
        return cell;
    }


    private void showEventDialog(LocalDate date) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Events for " + date);
        dialog.setHeaderText("Manage events for " + date);

        // Show existing events
        ListView<String> eventListView = new ListView<>();
        eventListView.setPrefHeight(120);
        eventListView.getItems().addAll(events.getOrDefault(date, new ArrayList<>()));

        // Text field for adding new event
        TextField newEventField = new TextField();
        newEventField.setPromptText("Enter new event...");

        VBox vbox = new VBox(10,
                new Label("Existing Events:"),
                eventListView,
                new Label("Add New Event:"),
                newEventField
        );
        dialog.getDialogPane().setContent(vbox);

        // Buttons
        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        ButtonType deleteOneButton = new ButtonType("Delete Selected", ButtonBar.ButtonData.LEFT);
        ButtonType deleteAllButton = new ButtonType("Delete All", ButtonBar.ButtonData.LEFT);
        ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().addAll(addButton, deleteOneButton, deleteAllButton, closeButton);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == addButton) {
                String newEvent = newEventField.getText().trim();
                if (!newEvent.isEmpty()) {
                    events.computeIfAbsent(date, k -> new ArrayList<>()).add(newEvent);
                }
            } else if (buttonType == deleteOneButton) {
                String selected = eventListView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    events.get(date).remove(selected);
                    if (events.get(date).isEmpty()) {
                        events.remove(date);
                    }
                }
            } else if (buttonType == deleteAllButton) {
                events.remove(date);
            }
            return null;
        });

        dialog.showAndWait();
        saveEventsToFile();
        drawCalendar();

    }

    //File part
    private void saveEventsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(eventFile))) {
            for (Map.Entry<LocalDate, List<String>> entry : events.entrySet()) {
                LocalDate date = entry.getKey();
                for (String event : entry.getValue()) {
                    writer.write(date + "|" + event);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadEventsFromFile() {
        events.clear();
        if (!eventFile.exists()) return;

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
