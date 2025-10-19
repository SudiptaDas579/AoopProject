package org.example.aoopproject;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.util.Duration;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class PlacesAutoSuggest {

    private final TextField textField;
    private final String apiKey;

    private final Popup popup;
    private final ListView<PlaceResult> listView;

    private volatile PlaceResult lastSelectedPlace = null;

    private final PauseTransition debounce;
    private Task<List<PlaceResult>> currentTask = null;
    private static final int DEBOUNCE_MS = 300;

    private final HttpClient httpClient;

    public PlacesAutoSuggest(TextField textField, String apiKey) {
        this.textField = textField;
        this.apiKey = apiKey;
        this.httpClient = HttpClient.newHttpClient();

        this.popup = new Popup();
        this.popup.setAutoHide(true);
        this.popup.setAutoFix(true);

        this.listView = new ListView<>();
        this.listView.setPrefWidth(420);
        this.listView.setPrefHeight(220);

        // Two-line cell factory
        this.listView.setCellFactory(lv -> new ListCell<>() {
            private final VBox vbox = new VBox(2);
            private final Label title = new Label();
            private final Label subtitle = new Label();

            {
                title.setStyle("-fx-font-weight: bold;");
                subtitle.setStyle("-fx-font-size: 11px;");
                vbox.getChildren().addAll(title, subtitle);
            }

            @Override
            protected void updateItem(PlaceResult item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    title.setText(item.name != null ? item.name : item.description);
                    subtitle.setText(item.description != null ? item.description : "");
                    setGraphic(vbox);
                }
            }
        });

        popup.getContent().add(listView);

        // Debounce
        this.debounce = new PauseTransition(Duration.millis(DEBOUNCE_MS));
        this.debounce.setOnFinished(ev -> {
            String text = textField.getText();
            if (text == null || text.trim().isEmpty()) return;

            if (currentTask != null && !currentTask.isDone()) currentTask.cancel(true);
            currentTask = createFetchTask(text.trim());
            new Thread(currentTask).start();
        });

        installListeners();
    }

    public PlaceResult getSelectedPlace() {
        return lastSelectedPlace;
    }

    private void installListeners() {
        textField.textProperty().addListener((obs, oldV, newV) -> {
            if (newV == null || newV.trim().isEmpty()) {
                hidePopup();
                debounce.stop();
                if (currentTask != null && !currentTask.isDone()) currentTask.cancel(true);
                return;
            }
            debounce.playFromStart();
        });

        textField.setOnKeyPressed(ev -> {
            if (popup.isShowing() && ev.getCode() == KeyCode.DOWN) {
                listView.requestFocus();
                listView.getSelectionModel().selectFirst();
                ev.consume();
            }
        });

        listView.setOnKeyPressed(ev -> {
            if (ev.getCode() == KeyCode.ENTER) {
                PlaceResult sel = listView.getSelectionModel().getSelectedItem();
                if (sel != null) selectPlace(sel);
                ev.consume();
            } else if (ev.getCode() == KeyCode.ESCAPE) {
                hidePopup();
                ev.consume();
            }
        });

        listView.setOnMouseClicked(ev -> {
            PlaceResult sel = listView.getSelectionModel().getSelectedItem();
            if (sel != null) selectPlace(sel);
        });

        textField.focusedProperty().addListener((obs, oldV, newV) -> {
            if (!newV) Platform.runLater(this::hidePopup);
        });
    }

    private void showResults(List<PlaceResult> results) {
        if (results == null || results.isEmpty()) {
            hidePopup();
            return;
        }
        listView.getItems().setAll(results);
        if (!popup.isShowing()) {
            Window window = textField.getScene() != null ? textField.getScene().getWindow() : null;
            if (window == null) return;
            Point2D p = textField.localToScene(0.0, 0.0);
            double x = window.getX() + textField.getScene().getX() + p.getX();
            double y = window.getY() + textField.getScene().getY() + p.getY() + textField.getHeight();
            popup.show(window, x, y);
        }
    }

    private void hidePopup() {
        if (popup.isShowing()) popup.hide();
    }

    private void selectPlace(PlaceResult place) {
        textField.setText(place.description != null ? place.description : place.name);
        lastSelectedPlace = place;
        hidePopup();
    }

    private Task<List<PlaceResult>> createFetchTask(String input) {
        return new Task<>() {
            @Override
            protected List<PlaceResult> call() throws Exception {
                if (isCancelled()) return new ArrayList<>();

                List<PlaceResult> dhakaResults = fetchAutocomplete(input + " Dhaka");
                if (isCancelled()) return new ArrayList<>();
                if (!dhakaResults.isEmpty()) return dhakaResults;

                if (isCancelled()) return new ArrayList<>();
                return fetchAutocomplete(input + " Bangladesh");
            }

            @Override
            protected void succeeded() {
                List<PlaceResult> res = getValue();
                Platform.runLater(() -> showResults(res));
            }

            @Override
            protected void cancelled() {
                // keep popup visible
            }

            @Override
            protected void failed() {
                getException().printStackTrace();
                Platform.runLater(PlacesAutoSuggest.this::hidePopup);
            }
        };
    }

    private List<PlaceResult> fetchAutocomplete(String inputText) {
        List<PlaceResult> results = new ArrayList<>();
        try {
            String encodedInput = URLEncoder.encode(inputText, StandardCharsets.UTF_8);
            String urlStr = String.format(
                    "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=%s&types=geocode&language=en&components=country:bd&key=%s",
                    encodedInput, URLEncoder.encode(apiKey, StandardCharsets.UTF_8)
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlStr))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();

            JSONObject root = new JSONObject(body);
            String status = root.optString("status", "");
            if (!"OK".equals(status) && !"ZERO_RESULTS".equals(status)) {
                System.err.println("Autocomplete API status: " + status);
            }

            JSONArray predictions = root.optJSONArray("predictions");
            if (predictions == null) return results;

            for (int i = 0; i < predictions.length(); i++) {
                JSONObject obj = predictions.getJSONObject(i);
                String description = obj.optString("description", null);
                JSONObject structured = obj.optJSONObject("structured_formatting");
                String mainText = structured != null ? structured.optString("main_text", null) : null;
                String placeId = obj.optString("place_id", null);

                results.add(new PlaceResult(mainText, description, placeId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    public static class PlaceResult {
        public final String name; // main_text
        public final String description; // full address
        public final String placeId;

        public PlaceResult(String name, String description, String placeId) {
            this.name = name;
            this.description = description;
            this.placeId = placeId;
        }

        @Override
        public String toString() {
            return "PlaceResult{" +
                    "name='" + name + '\'' +
                    ", description='" + description + '\'' +
                    ", placeId='" + placeId + '\'' +
                    '}';
        }
    }
}
