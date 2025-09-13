package org.example.aoopproject;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class homepageController implements Initializable {

    @FXML
    public AnchorPane homepageView;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Image view = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/pictures/resized.png")));
        BackgroundImage viewBG = new BackgroundImage(
                view,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
        );
        homepageView.setBackground(new Background(viewBG));
    }
}
