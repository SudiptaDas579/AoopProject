package org.example.aoopproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.IOException;

public class NewsPortalController {

    @FXML
    private WebView newsWeb;
    @FXML
    private Pane newsListPane;
    @FXML
    private Pane newsPane;
    @FXML
    private Button backButton;

    private Scene scene;
    private Stage stage;


    @FXML
    private ImageView prothomAloImg, dailyStarImg, dhakaTribuneImg, kalerKanthoImg;

    private WebEngine webEngine;

    @FXML
    public void initialize() {
        webEngine = newsWeb.getEngine();
        webEngine.setJavaScriptEnabled(true);
        webEngine.loadContent("<meta charset='UTF-8'>");

        newsPane.setVisible(false);

        // img click
        prothomAloImg.setOnMouseClicked(event -> openWebsite("https://www.prothomalo.com"));
        dailyStarImg.setOnMouseClicked(event -> openWebsite("https://www.thedailystar.net"));
        dhakaTribuneImg.setOnMouseClicked(event -> openWebsite("https://www.dhakatribune.com"));
        kalerKanthoImg.setOnMouseClicked(event -> openWebsite("https://www.kalerkantho.com"));


        backButton.setOnAction(event -> {
            newsPane.setVisible(false);
            newsListPane.setVisible(true);
            newsWeb.getEngine().load(null);
        });
    }

    private void openWebsite(String url) {
        newsListPane.setVisible(false);
        newsPane.setVisible(true);
        webEngine.load(url);
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
        ThemeManager.setScene(scene);


    }

}
