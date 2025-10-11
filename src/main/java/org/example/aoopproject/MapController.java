package org.example.aoopproject;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class MapController implements Initializable {

    @FXML private WebView mapView;
    @FXML private TextField fromField;
    @FXML private TextField toField;
    @FXML private VBox infoPane;
    @FXML private ListView<String> infoList;

    private WebEngine webEngine;
    private JSObject jsWindow;

    private HashSet<CompanyList> companySet = new HashSet<>();
    private Map<String, double[]> busStopCoordinates = new HashMap<>();

    // Single ContextMenu per TextField
    private final Map<TextField, ContextMenu> fieldMenus = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Load companies from BusFileHandler thread
        BusFileHandler fileHandler = new BusFileHandler();
        fileHandler.start();
        try {
            fileHandler.join(); // wait for thread to finish
            companySet = fileHandler.companyLists;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Load WebView map
        webEngine = mapView.getEngine();
        URL mapUrl = getClass().getResource("/org/example/aoopproject/map.html");
        if(mapUrl != null){
            webEngine.load(mapUrl.toExternalForm());
        } else {
            System.err.println("map.html not found in resources!");
        }

        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if(newState == javafx.concurrent.Worker.State.SUCCEEDED){
                jsWindow = (JSObject) webEngine.executeScript("window");
                jsWindow.setMember("javaConnector", new JavaConnector());
            }
        });

        if(companySet != null && !companySet.isEmpty()) {
            loadBusStopCoordinates();
        }

        loadUserLocation();
        setupAutocomplete();
    }

    private void loadBusStopCoordinates(){
        for(CompanyList company : companySet){
            if(company.getBusStopages() == null) continue;
            for(Map.Entry<Integer, String> entry : company.getBusStopages().entrySet()){
                if(!busStopCoordinates.containsKey(entry.getValue())){
                    double lat = 23.8103 + entry.getKey() * 0.001;
                    double lng = 90.4125 + entry.getKey() * 0.001;
                    busStopCoordinates.put(entry.getValue(), new double[]{lat, lng});
                }
            }
        }
    }

    private void loadUserLocation() {
        fromField.setText("Dhaka, Bangladesh");
    }

    private void setupAutocomplete() {
        List<String> stopNames = new ArrayList<>(busStopCoordinates.keySet());
        fromField.textProperty().addListener((obs, oldText, newText) -> suggestStops(fromField, stopNames));
        toField.textProperty().addListener((obs, oldText, newText) -> suggestStops(toField, stopNames));
    }

    private void suggestStops(TextField field, List<String> stops) {
        String query = field.getText();
        if(query.isEmpty()) {
            if(fieldMenus.containsKey(field)) fieldMenus.get(field).hide();
            return;
        }

        List<String> suggestions = stops.stream()
                .filter(name -> name.toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
        if(suggestions.isEmpty()){
            if(fieldMenus.containsKey(field)) fieldMenus.get(field).hide();
            return;
        }

        ContextMenu menu = fieldMenus.computeIfAbsent(field, k -> new ContextMenu());
        menu.getItems().clear();

        for(String s : suggestions){
            MenuItem item = new MenuItem(s);
            item.setOnAction(e -> {
                field.setText(s);
                menu.hide();
            });
            menu.getItems().add(item);
        }

        if(!menu.isShowing()) {
            menu.show(field, Side.BOTTOM, 0, 0);
        }
    }

    @FXML
    private void onFindRoute() {
        String fromText = fromField.getText();
        String toText = toField.getText();
        if(fromText.isEmpty() || toText.isEmpty()) return;
        if(!busStopCoordinates.containsKey(fromText) || !busStopCoordinates.containsKey(toText)) return;

        List<CompanyList> servingCompanies = companySet.stream()
                .filter(c -> c.getBusStopages() != null
                        && c.getBusStopages().containsValue(fromText)
                        && c.getBusStopages().containsValue(toText))
                .collect(Collectors.toList());

        Platform.runLater(() -> {
            List<String> displayInfo = new ArrayList<>();
            for(CompanyList company : servingCompanies){
                StringBuilder sb = new StringBuilder();
                sb.append(company.getCompanyName()).append("\n");
                sb.append("Fare: ").append(getFare(company, fromText, toText)).append("\n");
                sb.append("Buses: ");
                if(company.getBusInfo() != null && !company.getBusInfo().isEmpty()){
                    sb.append(company.getBusInfo().stream()
                            .map(BusInformation::getBusNo)
                            .collect(Collectors.joining(", ")));
                }
                displayInfo.add(sb.toString());
            }
            infoList.setItems(FXCollections.observableArrayList(displayInfo));
            infoPane.setVisible(true);
        });

        // Clear previous markers
        jsWindow.call("receiveFromJava", "clearMarkers", null);

        List<Map<String, Object>> routeLocations = new ArrayList<>();
        for(String stop : busStopCoordinates.keySet()){
            if(isRelevantStop(fromText, toText, stop)){
                double[] coords = busStopCoordinates.get(stop);
                Map<String, Object> loc = Map.of(
                        "lat", coords[0],
                        "lng", coords[1],
                        "title", stop
                );
                routeLocations.add(loc);
                jsWindow.call("receiveFromJava", "addMarker", loc);
            }
        }

        routeLocations.sort(Comparator.comparingDouble(loc -> Math.abs((double)loc.get("lat") - busStopCoordinates.get(fromText)[0])));
        jsWindow.call("receiveFromJava", "drawRoute", routeLocations);
    }

    private boolean isRelevantStop(String from, String to, String current){
        return current.equals(from) || current.equals(to) || true;
    }

    private String getFare(CompanyList company, String from, String to){
        if(company.getBusStopages() == null || company.getFareList() == null) return "N/A";
        Integer fromKey = company.getBusStopages().entrySet().stream()
                .filter(e -> e.getValue().equalsIgnoreCase(from))
                .map(Map.Entry::getKey).findFirst().orElse(null);
        Integer toKey = company.getBusStopages().entrySet().stream()
                .filter(e -> e.getValue().equalsIgnoreCase(to))
                .map(Map.Entry::getKey).findFirst().orElse(null);
        if(fromKey != null && toKey != null){
            return company.getFareList().getOrDefault(fromKey, "N/A") + " - " +
                    company.getFareList().getOrDefault(toKey, "N/A");
        }
        return "N/A";
    }

    public class JavaConnector {
        public void onRouteCalculated(String orderJson){
            System.out.println("Optimized order from JS: " + orderJson);
        }
    }
}
