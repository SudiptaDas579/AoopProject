package org.example.aoopproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class AdminController implements Initializable  {

    @FXML
    public TextField EnterCompanyName;
    public TextField BusStopages;
    public TextField EnterFareLists;
    public TextArea goooo;
    @FXML
    public Pane addNewBus;
    public Pane busInfo;
    public Pane addingTheCompany;
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
    public Label busInfoShow;
    public Button AddService;
    public Button addNewInformation;
    public AnchorPane AdminPane;
    public Stage stage;
    public Scene scene;



    public HashSet<CompanyList> companyLists=new HashSet<>();
    public HashSet<BusInformation> busInformations=new HashSet<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private ListView<String> suggestionListView;

    public CompanyList SelectedCompany;

    private GooglePlacesService placesService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        setBG();

        final String apiKey = "AIzaSyCqbKdjkod9FVs371m7I4Vv3B7opV2xfWI";

        File file = new File("src/main/java/org/example/aoopproject/files/CompanyList.txt");

        BusFileHandler busFileHandler = new BusFileHandler();
        companyLists = busFileHandler.getCompanyLists(file);

        CompanyButton();

        placesService = new GooglePlacesService();

        new BusStopagesAutoComplete(BusStopages, placesService);
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
        AdminPane.setBackground(new Background(viewBG));
    }

    public void logOut(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
        stage =(Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);

    }


    @FXML
    public void addTheCompany() {

        if (EnterCompanyName.getText().isEmpty() || BusStopages.getText().isEmpty() || EnterFareLists.getText().isEmpty()) {

            companyInfoShow.setText("Please fill all the fields");
        }
        else {

            String[] stopages = BusStopages.getText().split("-");

            String[] fareList = EnterFareLists.getText().split("-");

            HashMap<Integer, String> stopage = new HashMap<>();
            HashMap<Integer, String> fare = new HashMap<>();

            for (int i = 0; i < stopages.length; i++) {
                stopage.put(i, stopages[i]);
            }
            for (int i = 0; i < fareList.length; i++) {
                fare.put(i, fareList[i]);
            }

            CompanyList NewCompanyList = new CompanyList(EnterCompanyName.getText(), stopage, fare,busInformations );
            companyLists.add(NewCompanyList);

            companyInfoShow.setText("Company Information Added successfully!");

            File file = new File("src/main/java/org/example/aoopproject/files/CompanyList.txt");
            BusFileHandler busFileHandler = new BusFileHandler();
            busFileHandler.updateInFile(file, companyLists);

            CompanyButton();


        }
    }

    @FXML
    public void addNewBus(){
        addingTheCompany.setVisible(false);
        addNewBus.setVisible(true);

        busInfo.setPrefHeight(254);
        addNewInformation.setLayoutY(215);
        goooo.setPrefHeight(148);


    }



    public void CompanyButton(){


        companylistPane.getChildren().clear();

        for (CompanyList companyList : companyLists) {

            String CompanyName= companyList.getCompanyName();
            Button button=new Button(CompanyName);
            button.setPrefWidth(150);
            button.setPrefHeight(60);
            button.setOnAction(this::selectedCompanyButton);
            companylistPane.getChildren().add(button);

        }

    }

    @FXML
    public void addNewService(){

        companyPane.setPrefWidth( 285);

        BusCompanies.setLayoutX(61);
        BusCompanies.setLayoutY(25);

        AddService.setLayoutX(50);
        AddService.setLayoutY(600);

        companylistPane.setLayoutX(40);
        companylistPane.setLayoutY(84);

        addingTheCompany.setVisible(true);
        busInfo.setVisible(true);
        busInfo.setPrefHeight(254);
        addNewInformation.setLayoutY(215);
        goooo.setPrefHeight(148);

        addNewBus.setVisible(false);
    }

    public void selectedCompanyButton(ActionEvent e) {

        companyPane.setPrefWidth( 285);

        BusCompanies.setLayoutX(61);
        BusCompanies.setLayoutY(25);

        AddService.setLayoutX(50);
        AddService.setLayoutY(629);

        companylistPane.setLayoutX(40);
        companylistPane.setLayoutY(84);

        addingTheCompany.setVisible(false);
        busInfo.setVisible(true);

        addNewBus.setVisible(false);

        busInfo.setPrefHeight(684);
        addNewInformation.setLayoutY(645);
        goooo.setPrefHeight(572);

        Button companyButton =((Button) e.getSource());

        for (CompanyList companyList : companyLists) {

            String CompanyName= companyList.getCompanyName();


            if(companyButton.getText().equals(CompanyName)){
                SelectedCompany=companyList;
                goooo.setText("\n"+companyList);

            }
        }

    }
    public void addTheNewBus(){

        companyPane.setPrefWidth( 285);

        BusCompanies.setLayoutX(61);
        BusCompanies.setLayoutY(25);

        AddService.setLayoutX(143);
        AddService.setLayoutY(629);

        companylistPane.setLayoutX(60);
        companylistPane.setLayoutY(84);

        addingTheCompany.setVisible(false);
        busInfo.setVisible(true);
        busInfo.setPrefHeight(254);
        addNewInformation.setLayoutY(215);
        goooo.setPrefHeight(148);

        addNewBus.setVisible(true);

        if (busPlateNumber.getText().isEmpty() ||driverName.getText().isEmpty() || driverLicense.getText().isEmpty()|| phoneNumber.getText().isEmpty()|| seatCapacity.getText().isEmpty()) {

            busInfoShow.setText("Please fill all the fields");

        }
        else{

            BusInformation busInformation=new BusInformation(busPlateNumber.getText(),driverName.getText(),driverLicense.getText(),phoneNumber.getText(),seatCapacity.getText());
            for (CompanyList CompanyList : companyLists) {
                if (CompanyList.equals(SelectedCompany))

                {
                    System.out.println("fuuuuuu");

                    CompanyList.getBusInfo().add(busInformation);
                    goooo.setText("Company: " + SelectedCompany.getCompanyName() +
                            "\nStops: " + SelectedCompany.busStoppageName() +
                            "\nFares: " + SelectedCompany.fareListName() +
                            "\nBuses:\n" + SelectedCompany.getBusInfo().stream()
                            .map(BusInformation::toString)
                            .collect(Collectors.joining("\n\n")));

                    busInfoShow.setText("Bus Information Added successfully!");




                }
            }
            File file = new File("src/main/java/org/example/aoopproject/files/CompanyList.txt");


            BusFileHandler busFileHandler = new BusFileHandler();

            busFileHandler.updateInFile(file,companyLists);

        }

    }



}