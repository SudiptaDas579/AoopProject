package org.example.aoopproject;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.net.URL;
import java.util.ResourceBundle;

public class MapController implements Initializable {

    @FXML
    private WebView webView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        WebEngine webEngine = webView.getEngine();
        webEngine.setJavaScriptEnabled(true);

        String mapUrl = getClass().getResource("/org/example/aoopproject/Map.html").toExternalForm();
        webEngine.load(mapUrl);
        System.out.println("Map loaded with directions + autocomplete features.");
    }
}
