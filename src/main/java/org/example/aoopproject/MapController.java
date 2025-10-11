package org.example.aoopproject;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import javafx.concurrent.Worker.State;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MapController implements Initializable {

    @FXML
    private WebView mapWebView;
    private WebEngine webEngine;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private ListView<String> suggestionListView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        suggestionListView = new ListView<>();
        suggestionListView.setVisible(false);
        suggestionListView.setPrefHeight(100);
        suggestionListView.setPrefWidth(BusStopages.getPrefWidth());
        ((Pane) BusStopages.getParent()).getChildren().add(suggestionListView);


        BusStopages.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            String input = BusStopages.getText().trim();
            if (input.length() < 2) {
                suggestionListView.setVisible(false);
                return;
            }

            PlaceSuggestionTask task = new PlaceSuggestionTask(input, apiKey);

            task.setOnSucceeded(e -> {
                List<String> suggestions = task.getValue();
                Platform.runLater(() -> {
                    suggestionListView.getItems().setAll(suggestions);
                    suggestionListView.setVisible(!suggestions.isEmpty());
                });
            });

            executor.submit(task);
        });

        suggestionListView.setFocusTraversable(false);

        suggestionListView.setOnMouseClicked(e -> {
            String selected = suggestionListView.getSelectionModel().getSelectedItem();
            if (selected == null) return;

            String currentText = BusStopages.getText();
            if (currentText == null) currentText = "";

            int lastDash = currentText.lastIndexOf('-');
            String newText;
            if (lastDash == -1) {
                newText = selected + "-";
            } else {
                String prefix = currentText.substring(0, lastDash + 1);
                newText = prefix + selected + "-";
            }

            BusStopages.setText(newText);


            suggestionListView.setVisible(false);
            suggestionListView.getSelectionModel().clearSelection();

            Platform.runLater(() -> {
                BusStopages.requestFocus();
                BusStopages.end();
            });
        });

        webEngine = mapWebView.getEngine();

        // 1. Load the HTML file from your resources folder
        URL htmlUrl = getClass().getResource("org/example/aoopproject/Map.html");
        if (htmlUrl != null) {
            webEngine.load(htmlUrl.toExternalForm());
        } else {
            // Handle error: HTML file not found
            System.err.println("Error: google_map.html not found in resources!");
        }

        // 2. Wait for the map to load before executing JavaScript
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == State.SUCCEEDED) {
                // Now you can call JavaScript functions safely
                // Example: Move the map to New York
                updateMapLocation(40.7128, -74.0060);
            }
        });
    }

    // 3. Method to update the map from Java
    public void updateMapLocation(double latitude, double longitude) {
        // Call the JavaScript function 'updateMarker' defined in your HTML
        String script = String.format("updateMarker(%f, %f);", latitude, longitude);
        webEngine.executeScript(script);
    }
}