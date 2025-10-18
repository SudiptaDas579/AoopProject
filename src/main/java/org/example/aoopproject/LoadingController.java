package org.example.aoopproject;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class LoadingController implements Initializable {

    @FXML
    public AnchorPane loadingPane;
    public ImageView loadingView;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        setBG();
    }

    public void setBG(){
        Image view = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/pictures/loading.gif")));
//        BackgroundImage viewBG = new BackgroundImage(
//                view,
//                BackgroundRepeat.NO_REPEAT,
//                BackgroundRepeat.NO_REPEAT,
//                BackgroundPosition.CENTER,
//                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
//        );
      //  loadingPane.setBackground(new Background(viewBG));
        loadingView.setImage(view);
    }

}
