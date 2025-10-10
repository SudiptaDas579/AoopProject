package org.example.aoopproject;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AuthorityController implements Initializable {

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

    @FXML
    public TextField busPlateNumber;
    public TextField driverName;
    public TextField driverLicense;
    public TextField phoneNumber;
    public TextField seatCapacity;

    public Label companyInfoShow;

    public HashSet<CompanyList> companyLists = new HashSet<>();

    // 🔹 Executor for background tasks
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    // 🔹 Suggestion dropdown
    private ListView<String> suggestionListView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        final String apiKey = "AIzaSyCqbKdjkod9FVs371m7I4Vv3B7opV2xfWI";
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

        // 🔹 Create the suggestion list and position it under BusStopages
        suggestionListView = new ListView<>();
        suggestionListView.setVisible(false);
        suggestionListView.setPrefHeight(100);
        suggestionListView.setPrefWidth(BusStopages.getPrefWidth());
        ((Pane) BusStopages.getParent()).getChildren().add(suggestionListView);

        // 🔹 Listen for text changes to trigger place suggestions
        BusStopages.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            String input = BusStopages.getText().trim();
            if (input.length() < 2) {
                suggestionListView.setVisible(false);
                return;
            }

            PlaceSuggestionTask task = new PlaceSuggestionTask(input, apiKey);

            task.setOnSucceeded(e -> {
                List<String> suggestions = task.getValue();
                Platform.runLater(() -> {
                    suggestionListView.getItems().setAll(suggestions);
                    suggestionListView.setVisible(!suggestions.isEmpty());
                });
            });

            executor.submit(task);
        });

        // 🔹 When user clicks on a suggestion
        // Prevent the ListView from taking focus
        suggestionListView.setFocusTraversable(false);

        suggestionListView.setOnMouseClicked(e -> {
            String selected = suggestionListView.getSelectionModel().getSelectedItem();
            if (selected == null) return;

            String currentText = BusStopages.getText();
            if (currentText == null) currentText = "";

            // Replace last segment (after last '-') with the selected suggestion
            int lastDash = currentText.lastIndexOf('-');
            String newText;
            if (lastDash == -1) {
                newText = selected + "-";
            } else {
                String prefix = currentText.substring(0, lastDash + 1);
                newText = prefix + selected + "-";
            }

            BusStopages.setText(newText);

            // Hide suggestions
            suggestionListView.setVisible(false);
            suggestionListView.getSelectionModel().clearSelection();

            // Refocus TextField and move caret to the end after small delay
            Platform.runLater(() -> {
                BusStopages.requestFocus();
                BusStopages.end(); // moves caret to the end
            });
        });







    }

    @FXML
    public void addTheCompany() {
        if (EnterCompanyName.getText().isEmpty() || BusStopages.getText().isEmpty() || EnterFareLists.getText().isEmpty()) {
            companyInfoShow.setText("Please fill all the fields");
        } else {
            String[] stopages = BusStopages.getText().split("-");
            String[] fareList = EnterFareLists.getText().split("-");

            HashMap<Integer, String> stopage = new HashMap<>();
            HashMap<Integer, String> fare = new HashMap<>();

            for (int i = 0; i < stopages.length; i++) stopage.put(i, stopages[i]);
            for (int i = 0; i < fareList.length; i++) fare.put(i, fareList[i]);

            CompanyList NewBuslist = new CompanyList(EnterCompanyName.getText(), stopage, fare, null);

            File file = new File("src/main/java/org/example/aoopproject/files/CompanyList.txt");
            BusFileHandler busFileHandler = new BusFileHandler();
            busFileHandler.start();
            try {
                busFileHandler.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            companyLists.add(NewBuslist);
            companyInfoShow.setText("Company Information Added successfully!");

            HashSet<CompanyList> newOne = new HashSet<>();
            newOne.add(NewBuslist);
            busFileHandler.updateInFile(file, newOne);

            StringBuilder stringBuilder = new StringBuilder();
            for (CompanyList busList : companyLists) {
                stringBuilder.append(busList).append("\n");
            }
            goooo.setText(stringBuilder.toString());
        }
    }

    @FXML
    public void addNewBus() {
        BusInformation busInformation = new BusInformation(
                busPlateNumber.getText(),
                driverName.getText(),
                driverLicense.getText(),
                Integer.parseInt(phoneNumber.getText()),
                seatCapacity.getText()
        );
    }

    public void CompanyButton() {
        for (CompanyList companyList : companyLists) {
            String CompanyName = companyList.getCompanyName();
            Button button = new Button(CompanyName);
            button.setPrefWidth(150);
            button.setPrefHeight(150);
            companylistPane.getChildren().add(button);
        }
    }
}
