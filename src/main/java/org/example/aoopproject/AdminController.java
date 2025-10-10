package org.example.aoopproject;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ResourceBundle;

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
    public Pane companylistPane;


    @FXML
    public TextField busPlateNumber;
    public TextField driverName;
    public TextField driverLicense;
    public TextField phoneNumber;
    public TextField seatCapacity;



    public HashSet<CompanyList> companyLists=new HashSet<CompanyList>();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        File file = new File("src/main/java/org/example/aoopproject/files/CompanyList.txt");

        BusFileHandler busFileHandler = new BusFileHandler();
        busFileHandler.start();

        try {
            busFileHandler.join();
        } catch (InterruptedException e) {

            throw new RuntimeException(e);
        }

        companyLists = busFileHandler.getBusLists(file);

        CompanyButton();
    }


        @FXML
    public void addTheCompany(){

        String[] stopages = BusStopages.getText().split("-");
        String[] fareList = EnterFareLists.getText().split("-");

        HashMap<Integer,String> stopage = new HashMap<>();
        HashMap<Integer,String> fare = new HashMap<>();

        for(int i=0;i<stopages.length;i++){
            stopage.put(i,stopages[i]);
        }
        for(int i=0;i<fareList.length;i++){
            fare.put(i,fareList[i]);
        }

        CompanyList NewBuslist = new CompanyList(EnterCompanyName.getText(),stopage,fare,null);

        File file = new File("src/main/java/org/example/aoopproject/files/CompanyList.txt");


        BusFileHandler busFileHandler = new BusFileHandler();
        busFileHandler.start();
        try {
            busFileHandler.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        companyLists.add(NewBuslist);


        busFileHandler.updateInFile(file,companyLists);
        StringBuilder stringBuilder=new StringBuilder();
        for (CompanyList busList : companyLists) {
            stringBuilder.append(busList.toString());
            stringBuilder.append("\n");

        }
        goooo.setText(stringBuilder.toString());
    }



    @FXML
    public void addNewBus(){

        BusInformation busInformation=new BusInformation(busPlateNumber.getText(),driverName.getText(),driverLicense.getText(),Integer.parseInt(phoneNumber.getText()),seatCapacity.getText());


    }
    public void CompanyButton(){

        for (CompanyList companyList : companyLists) {

            String CompanyName= companyList.getCompanyName();
            Button button=new Button(CompanyName);
            companylistPane.getChildren().add(button);

        }

    }

}
