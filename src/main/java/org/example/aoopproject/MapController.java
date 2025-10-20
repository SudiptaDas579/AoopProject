package org.example.aoopproject;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MapController implements Initializable {

    @FXML
    private WebView webView;

    public Stage stage;
    public Scene scene;
    public AnchorPane mapPane;

    @FXML
    public TextField origin;
    public TextField destination;
    public Pane InfoShow;

    public HashSet<CompanyList> companies = new HashSet<>();

    GooglePlacesService placesService;
    StopLocationCache stopCache;
    BusSearchService busSearchService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setBG();

        File file = new File("src/main/java/org/example/aoopproject/files/CompanyList.txt");
        BusFileHandler busFileHandler = new BusFileHandler();
        companies = busFileHandler.getCompanyLists(file);


        placesService = new GooglePlacesService();
        stopCache = new StopLocationCache();
        busSearchService = new BusSearchService(companies, stopCache, placesService, InfoShow);

        new MapAutoSuggestion(origin, placesService);
        new MapAutoSuggestion(destination, placesService);

//        WebEngine webEngine = webView.getEngine();
//        webEngine.setJavaScriptEnabled(true);
//        webEngine.setOnAlert(event -> System.out.println("JS Alert: " + event.getData()));
//
//        String mapUrl = Objects.requireNonNull(getClass().getResource("/org/example/aoopproject/Map.html")).toExternalForm();
//        webEngine.load(mapUrl);
//
//        System.out.println("Map loaded with Google Maps v3.57 and transit features.");
    }

    @FXML
    public void searchButtonClicked(ActionEvent event) {
        // Show the InfoShow pane when Bus button is clicked
        InfoShow.setVisible(true);

        // Resolve nearest stops
        String originStop = busSearchService.resolveNearestStop(origin.getText());
        String destStop = busSearchService.resolveNearestStop(destination.getText());

        // Perform search and display company buttons
        busSearchService.searchRoute(originStop, destStop);
    }




    public void setBG(){
        Image view = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/pictures/lightHome.png")));
        BackgroundImage viewBG = new BackgroundImage(
                view,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
        );
        mapPane.setBackground(new Background(viewBG));
    }

    @FXML
    public void homePageSwitch(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("homepage.fxml"));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        ThemeManager.setScene(scene);
    }

    @FXML
    public void newsPortalSwitch(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("homepage.fxml"));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        ThemeManager.setScene(scene);
    }

    @FXML
    public void mapSwitch(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MapView.fxml"));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        ThemeManager.setScene(scene);
    }

    @FXML
    public void eventHolidaySwitch(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("homepage.fxml"));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        ThemeManager.setScene(scene);
    }

    @FXML
    public void logOut(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
    }
}
