package org.example.aoopproject;


import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Popup;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MapAutoSuggestion {

    private final TextField textField;
    private final GooglePlacesService placesService;
    private final Popup popup;
    private final ListView<String> suggestionListView;

    // Keep last fetched suggestions so we can check "was inserted from suggestions"
    private final List<String> lastSuggestions = new ArrayList<>();

    // A small sentinel shown when there are no results (still selectable but treated specially)
    private static final String NO_RESULTS = "No results found";

    public MapAutoSuggestion(TextField textField, GooglePlacesService placesService) {
        this.textField = Objects.requireNonNull(textField);
        this.placesService = Objects.requireNonNull(placesService);

        popup = new Popup();
        popup.setAutoHide(true);

        suggestionListView = new ListView<>();
        suggestionListView.setPrefWidth(Math.max(200, (int) textField.getPrefWidth()));
        suggestionListView.setPrefHeight(150);
        suggestionListView.setFocusTraversable(false);

        popup.getContent().add(suggestionListView);

        attachListeners();
    }

    private void attachListeners() {
        // Typing listener: Key released -> update suggestions when length >= 2
        textField.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            // If the key is ESC, we intentionally do nothing (per requirement C).
            if (event.getCode() == KeyCode.ESCAPE) {
                return;
            }

            String input = textField.getText();
            if (input == null) input = "";

            // Only fetch suggestions when >= 2 characters.
            if (input.length() >= 2) {
                updateSuggestions(input);
            } else {
                // If popup is already showing, keep it visible with old items (per your B choice).
                // If popup is not showing, do nothing (don't auto-show on short input).
                // So: do nothing here.
            }
        });

        // Keyboard navigation & Enter handling
        textField.setOnKeyPressed(event -> {
            if (!popup.isShowing() || suggestionListView.getItems().isEmpty()) return;

            int index = suggestionListView.getSelectionModel().getSelectedIndex();

            if (event.getCode() == KeyCode.DOWN) {
                if (index < suggestionListView.getItems().size() - 1) {
                    suggestionListView.getSelectionModel().select(index + 1);
                    suggestionListView.scrollTo(index + 1);
                }
                event.consume();
            } else if (event.getCode() == KeyCode.UP) {
                if (index > 0) {
                    suggestionListView.getSelectionModel().select(index - 1);
                    suggestionListView.scrollTo(index - 1);
                }
                event.consume();
            } else if (event.getCode() == KeyCode.ENTER) {
                insertSelected();
                event.consume();
            }
            // ESC intentionally ignored.
        });

        // Mouse click selection
        suggestionListView.setOnMouseClicked(e -> insertSelected());

        // Focus listener: if the current text equals one of the last suggestions, clear it.
        textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                String current = textField.getText();
                if (current == null) current = "";
                // Only clear if the current text equals one of the last fetched suggestions
                // (exact match). After clearing, we must NOT auto-show suggestions.
                if (!lastSuggestions.isEmpty() && lastSuggestions.contains(current)) {
                    textField.clear();
                    // Also clear the record of last suggestions because clearing implies we don't
                    // want the field to auto-clear again until new suggestions fetched.
                    lastSuggestions.clear();
                    // Ensure popup is hidden (if it was visible) but do NOT re-open it.
                    if (popup.isShowing()) popup.hide();
                }
            }
        });
    }

    private void updateSuggestions(String input) {
        // Request suggestions from the provided service (assumed Dhaka-first behavior inside).
        List<String> results = placesService.getSuggestions(input);

        // Update the lastSuggestions list (used later to detect if field value came from suggestions).
        lastSuggestions.clear();
        if (results != null && !results.isEmpty()) {
            lastSuggestions.addAll(results);
            suggestionListView.getItems().setAll(results);
        } else {
            // show the NO_RESULTS sentinel so UI indicates nothing found, but track no suggestions
            suggestionListView.getItems().setAll(NO_RESULTS);
            // keep lastSuggestions empty to avoid clearing on focus when user has typed something manually
        }

        // Select the first item so keyboard navigation works immediately.
        if (!suggestionListView.getItems().isEmpty()) {
            suggestionListView.getSelectionModel().selectFirst();
        }

        // Position popup under the text field
        if (!popup.isShowing()) {
            Bounds bounds = textField.localToScreen(textField.getBoundsInLocal());
            if (bounds != null) {
                popup.show(textField, bounds.getMinX(), bounds.getMaxY());
            }
        } else {
            // If popup already showing, we still may want to reposition in case window moved
            Bounds bounds = textField.localToScreen(textField.getBoundsInLocal());
            if (bounds != null) {
                popup.setX(bounds.getMinX());
                popup.setY(bounds.getMaxY());
            }
        }
    }

    private void insertSelected() {
        String selected = suggestionListView.getSelectionModel().getSelectedItem();
        if (selected == null || selected.equals(NO_RESULTS)) {
            // No valid selection — just hide popup and return
            popup.hide();
            return;
        }

        // Replace whole text with selected item.
        textField.setText(selected);

        // Close popup.
        popup.hide();

        Platform.runLater(() -> {
            textField.requestFocus();
            textField.positionCaret(textField.getText().length());
        });
    }


    public void hide() {
        if (popup.isShowing()) popup.hide();
        lastSuggestions.clear();
    }


    public boolean isShowing() {
        return popup.isShowing();
    }
}
