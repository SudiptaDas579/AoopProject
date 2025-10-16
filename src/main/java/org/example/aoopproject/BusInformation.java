package org.example.aoopproject;

import java.io.Serializable;

public class BusInformation implements Serializable {
    private String busNo;
    private String driverName;
    private String driverLicense;
    private String phoneNumber;
    private String capacity;




    public BusInformation(String busNo, String driverName, String driverLicense, String phoneNumber, String capacity) {
        this.busNo = busNo;
        this.driverName = driverName;
        this.driverLicense = driverLicense;
        this.phoneNumber = phoneNumber;
        this.capacity = capacity;
    }

    public String getBusNo() {
        return busNo;
    }

    public void setBusNo(String busNo) {
        this.busNo = busNo;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverLicense() {
        return driverLicense;
    }

    public void setDriverLicense(String driverLicense) {
        this.driverLicense = driverLicense;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }



    @Override
    public String toString() {
        return "BusInformation" +
                "\n Bus Number : " + busNo  +
                "\n Driver Name : " + driverName  +
                "\n Driver License : " + driverLicense  +
                "\n Driver's Phone Number : " + phoneNumber +
                "\n Seat Capacity : " + capacity ;
    }
}

