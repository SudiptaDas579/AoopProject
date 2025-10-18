package org.example.aoopproject;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Objects;

public class LoadingController {

    @FXML
    private AnchorPane loadingPane;

    @FXML
    public ImageView loadingView;

    @FXML
    public void initialize() {
        // Wait for 8 seconds, then move to login page
        PauseTransition delay = new PauseTransition(Duration.seconds(8.57));
        delay.setOnFinished(event -> switchToLogin());
        delay.play();

        setBG();
    }
    public void setBG(){
        Image view = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/pictures/loading.gif")));

        loadingView.setImage(view);

    }


    private void switchToLogin() {
        try {
            // Load the login.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();

            // Get the current window (stage)
            Stage stage = (Stage) loadingPane.getScene().getWindow();

            // Set the new scene (login page)
            stage.setScene(new Scene(root));
            stage.setTitle("Login Page");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
