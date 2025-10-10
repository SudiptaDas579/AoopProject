package org.example.aoopproject;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
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

    @FXML
    public TextField busPlateNumber;
    public TextField driverName;
    public TextField driverLicense;
    public TextField phoneNumber;
    public TextField seatCapacity;



    public HashSet<BusList> busLists=new HashSet<BusList>();
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

        BusList NewBuslist = new BusList(EnterCompanyName.getText(),stopage,fare,null);

        File file = new File("src/main/java/org/example/aoopproject/files/CompanyList.txt");


        BusFileHandler busFileHandler = new BusFileHandler();
        busFileHandler.start();
        try {
            busFileHandler.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        busLists.add(NewBuslist);


        busFileHandler.updateInFile(file,busLists);
        StringBuilder stringBuilder=new StringBuilder();
        for (BusList busList : busLists) {
            stringBuilder.append(busList.toString());
            stringBuilder.append("\n");

        }
        goooo.setText(stringBuilder.toString());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        File file = new File("src/main/java/org/example/aoopproject/files/CompanyList.txt");

        BusFileHandler busFileHandler = new BusFileHandler();
        busFileHandler.start();

        try {
            busFileHandler.join();
        }
        catch (InterruptedException e) {

            throw new RuntimeException(e);
        }

        busLists=busFileHandler.getBusLists(file);

    }
    @FXML
    public void addNewBus(){

        BusInformation busInformation=new BusInformation(busPlateNumber.getText(),driverName.getText(),driverLicense.getText(),Integer.parseInt(phoneNumber.getText()),seatCapacity.getText());


    }

}
