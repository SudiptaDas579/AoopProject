package org.example.aoopproject;

import java.io.Serializable;
import java.util.Objects;

public class Authority extends Users implements Serializable {
    private String CompanyName;
    private String DrivingLicense;
    private String BusNumber;
    public Authority(String name, String email, String password,String CompanyName, String DrivingLicense, String BusNumber) {
        super(name, email, password);
        this.CompanyName = CompanyName;
        this.DrivingLicense = DrivingLicense;
        this.BusNumber = BusNumber;
    }
    public Authority(String name, String email, String password) {
        super(name, email, password);
    }
    public String getCompanyName() {
        return CompanyName;
    }
    public String getDrivingLicense() {
        return DrivingLicense;
    }
    public String getBusNumber() {
        return BusNumber;
    }
    public void setCompanyName(String CompanyName) {
        this.CompanyName = CompanyName;
    }
    public void setDrivingLicense(String DrivingLicense) {
        this.DrivingLicense = DrivingLicense;
    }
    public void setBusNumber(String  BusNumber) {
        this.BusNumber = BusNumber;
    }

    @Override
    public String toString() {
        return "Authority{" +
                "CompanyName='" + CompanyName + '\'' +
                ", DrivingLicense='" + DrivingLicense + '\'' +
                ", BusNumber='" + BusNumber + '\'' +
                '}';
    }
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Authority)) return false;
        Authority authority= (Authority) o;
        //deciding based on email, or NID
        return Objects.equals(getEmail(), authority.getEmail())||Objects.equals(getPassword(), authority.getPassword());
//                || Objects.equals(DrivingLicense, authority.DrivingLicense)||
//                Objects.equals(BusNumber, authority.BusNumber);
    }

    @Override
    public int hashCode() {
        // to match equals()
        return Objects.hash(getEmail(), getPassword());
    }

}
