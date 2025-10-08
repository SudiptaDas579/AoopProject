package org.example.aoopproject;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.HashMap;

public class AdminController {
    @FXML
    public TextField EnterCompanyName;
    public TextField BusStopages;
    public TextField EnterFareLists;

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

        BusList busList = new BusList(EnterCompanyName.getText(),stopage,fare,null);

        BusFileHandler busFileHandler = new BusFileHandler();
        busFileHandler.start();
        try {
            busFileHandler.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
