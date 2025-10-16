package org.example.aoopproject;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.json.JSONArray;
import org.json.JSONObject;

public class AdminController implements Initializable {

    @FXML
    public TextField EnterCompanyName;
    public TextField BusStopages;
    public TextField EnterFareLists;
    public TextArea goooo;
    @FXML
    public Pane addNewBus;
    public Pane busInfo;
    public Pane addTheCompany;
    public VBox companylistPane;
    public Pane companyPane;

    @FXML
    public TextField busPlateNumber;
    public TextField driverName;
    public TextField driverLicense;
    public TextField phoneNumber;
    public TextField seatCapacity;

    public Label companyInfoShow;
    public Label BusCompanies;
    public Button AddService;

    public HashSet<CompanyList> companyLists = new HashSet<>();

    // === Popup for suggestions ===
    private Popup suggestionPopup;
    private ListView<String> suggestionList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        final String apiKey = "AIzaSyCqbKdjkod9FVs371m7I4Vv3B7opV2xfWI"; // 🔹 Replace this with your real key

        // === Load saved companies ===
        File file = new File("src/main/java/org/example/aoopproject/files/CompanyList.txt");
        BusFileHandler busFileHandler = new BusFileHandler();
        busFileHandler.start();
        try {
            busFileHandler.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        companyLists = busFileHandler.getCompanyLists(file);
        CompanyButton();

        // === Setup suggestion popup ===
        suggestionPopup = new Popup();
        suggestionList = new ListView<>();
        suggestionList.setStyle("-fx-background-color: white; -fx-border-color: #ccc; -fx-pref-width: 250px; -fx-pref-height: 250px;");
        suggestionPopup.getContent().add(suggestionList);

        // === Handle typing for suggestions ===
        BusStopages.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            String input = BusStopages.getText().trim();
            if (input.length() < 2) {
                suggestionPopup.hide();
                return;
            }

            Task<List<String>> task = new Task<>() {
                @Override
                protected List<String> call() throws Exception {
                    return fetchSuggestions(input, apiKey);
                }
            };

            task.setOnSucceeded(e -> {
                List<String> suggestions = task.getValue();
                if (suggestions == null || suggestions.isEmpty()) {
                    suggestionPopup.hide();
                    return;
                }

                Platform.runLater(() -> {
                    suggestionList.getItems().setAll(suggestions);
                    if (!suggestionPopup.isShowing()) {
                        double x = BusStopages.localToScreen(0, 0).getX();
                        double y = BusStopages.localToScreen(0, BusStopages.getHeight()).getY();
                        suggestionPopup.show(BusStopages, x, y);
                    }
                });
            });

            task.setOnFailed(e -> {
                suggestionPopup.hide();
                Throwable ex = task.getException();
                if (ex != null)
                    System.err.println("❌ Suggestion fetch error: " + ex.getMessage());
            });

            new Thread(task).start();
        });

        // === When user clicks a suggestion ===
        suggestionList.setOnMouseClicked(e -> {
            String selected = suggestionList.getSelectionModel().getSelectedItem();
            if (selected == null) return;
            BusStopages.setText(selected + "-");
            suggestionPopup.hide();
            BusStopages.requestFocus();
            BusStopages.end();
        });
    }

    // === Fetch suggestions from Google Places API ===
    private List<String> fetchSuggestions(String input, String apiKey) {
        List<String> results = new ArrayList<>();
        try {
            String encoded = URLEncoder.encode(input, "UTF-8");
            String urlString = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input="
                    + encoded + "&key=" + apiKey + "&components=country:bd";

            HttpURLConnection conn = (HttpURLConnection) new URL(urlString).openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) json.append(line);
            reader.close();

            JSONObject obj = new JSONObject(json.toString());
            if (!obj.has("predictions")) return results;

            JSONArray predictions = obj.getJSONArray("predictions");
            for (int i = 0; i < predictions.length(); i++) {
                String description = predictions.getJSONObject(i).getString("description");
                results.add(description);
            }
        } catch (Exception e) {
            System.err.println("⚠️ Failed to fetch suggestions: " + e.getMessage());
        }
        return results;
    }

    // === Add Company ===
    @FXML
    public void addTheCompany() {
        if (EnterCompanyName.getText().isEmpty() || BusStopages.getText().isEmpty() || EnterFareLists.getText().isEmpty()) {
            companyInfoShow.setText("Please fill all the fields");
            return;
        }

        String[] stopages = BusStopages.getText().split("-");
        String[] fareList = EnterFareLists.getText().split("-");

        HashMap<Integer, String> stopage = new HashMap<>();
        HashMap<Integer, String> fare = new HashMap<>();
        for (int i = 0; i < stopages.length; i++) stopage.put(i, stopages[i]);
        for (int i = 0; i < fareList.length; i++) fare.put(i, fareList[i]);

        CompanyList newCompany = new CompanyList(EnterCompanyName.getText(), stopage, fare, null);

        File file = new File("src/main/java/org/example/aoopproject/files/CompanyList.txt");
        BusFileHandler busFileHandler = new BusFileHandler();
        busFileHandler.start();
        try {
            busFileHandler.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        companyLists.add(newCompany);
        companyInfoShow.setText("✅ Company Information Added successfully!");

        HashSet<CompanyList> newOne = new HashSet<>();
        newOne.add(newCompany);
        busFileHandler.updateInFile(file, newOne);

        StringBuilder sb = new StringBuilder();
        for (CompanyList company : companyLists) sb.append(company.toString()).append("\n");
        goooo.setText(sb.toString());
    }

    @FXML
    public void addNewBus() {
        addTheCompany.setVisible(false);
        addNewBus.setVisible(true);
        new BusInformation(busPlateNumber.getText(), driverName.getText(), driverLicense.getText(),
                Integer.parseInt(phoneNumber.getText()), seatCapacity.getText());
    }

    public void CompanyButton() {
        for (CompanyList companyList : companyLists) {
            String companyName = companyList.getCompanyName();
            Button button = new Button(companyName);
            button.setPrefWidth(150);
            button.setPrefHeight(150);
            companylistPane.getChildren().add(button);
        }
    }

    @FXML
    public void addNewService() {
        companyPane.setPrefWidth(285);
        BusCompanies.setLayoutX(61);
        BusCompanies.setLayoutY(25);
        AddService.setLayoutX(143);
        AddService.setLayoutY(629);
        companylistPane.setLayoutX(60);
        companylistPane.setLayoutY(84);
        addTheCompany.setVisible(true);
        busInfo.setVisible(true);
        addNewBus.setVisible(false);
    }
}
