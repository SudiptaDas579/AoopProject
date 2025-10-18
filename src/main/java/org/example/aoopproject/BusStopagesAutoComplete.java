package org.example.aoopproject;

import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Popup;

import java.util.List;

public class BusStopagesAutoComplete {

    private final TextField textField;
    private final GooglePlacesService placesService;
    private final Popup popup;
    private final ListView<String> suggestionListView;

    private static final String NO_RESULTS = "No results found";

    public BusStopagesAutoComplete(TextField textField, GooglePlacesService placesService) {
        this.textField = textField;
        this.placesService = placesService;

        popup = new Popup();
        popup.setAutoHide(true);

        suggestionListView = new ListView<>();
        suggestionListView.setPrefWidth(textField.getPrefWidth());
        suggestionListView.setPrefHeight(150);
        suggestionListView.setFocusTraversable(false);

        popup.getContent().add(suggestionListView);

        attachListeners();
    }

    private void attachListeners() {
        // Typing listener
        textField.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            String input = getCurrentSegment();
            updateSuggestions(input);
        });

        // Keyboard navigation
        textField.setOnKeyPressed(event -> {
            if (!popup.isShowing() || suggestionListView.getItems().isEmpty()) return;

            int index = suggestionListView.getSelectionModel().getSelectedIndex();

            switch (event.getCode()) {
                case DOWN -> {
                    if (index < suggestionListView.getItems().size() - 1) {
                        suggestionListView.getSelectionModel().select(index + 1);
                        suggestionListView.scrollTo(index + 1);
                    }
                    event.consume();
                }
                case UP -> {
                    if (index > 0) {
                        suggestionListView.getSelectionModel().select(index - 1);
                        suggestionListView.scrollTo(index - 1);
                    }
                    event.consume();
                }
                case ENTER -> {
                    insertSelected();
                    event.consume();
                }
            }
        });

        // Mouse click selection
        suggestionListView.setOnMouseClicked(e -> insertSelected());
    }

    private String getCurrentSegment() {
        String text = textField.getText();
        if (text == null) text = "";
        int lastDash = text.lastIndexOf('-');
        return (lastDash == -1) ? text.trim() : text.substring(lastDash + 1).trim();
    }

    private void updateSuggestions(String input) {
        if (input.length() < 2) {
            popup.hide();
            return;
        }

        List<String> results = placesService.getSuggestions(input); // Dhaka first, then BD fallback

        if (results.isEmpty()) {
            suggestionListView.getItems().setAll(NO_RESULTS);
        } else {
            suggestionListView.getItems().setAll(results);
        }

        suggestionListView.getSelectionModel().selectFirst();

        // Position popup under the text field
        Bounds bounds = textField.localToScreen(textField.getBoundsInLocal());
        popup.show(textField, bounds.getMinX(), bounds.getMaxY());
    }

    private void insertSelected() {
        String selected = suggestionListView.getSelectionModel().getSelectedItem();
        if (selected == null || selected.equals(NO_RESULTS)) {
            popup.hide();
            return;
        }

        String currentText = textField.getText();
        if (currentText == null) currentText = "";

        int lastDash = currentText.lastIndexOf('-');
        String newText = (lastDash == -1) ? selected + "-" : currentText.substring(0, lastDash + 1) + selected + "-";

        textField.setText(newText);
        popup.hide();

        Platform.runLater(() -> {
            textField.requestFocus();
            textField.end();

            // Automatically trigger new suggestions for next segment
            String nextSegment = getCurrentSegment();
            updateSuggestions(nextSegment);
        });
    }
}
