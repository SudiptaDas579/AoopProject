package org.example.aoopproject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public class CompanyList implements Serializable {
    private String companyName;
    private HashMap<Integer,String> busStopages;
    private HashMap<Integer,String> fareList;
    private HashSet<BusInformation> busInfo;

    public CompanyList(String CompanyName, HashMap<Integer, String> busStopages, HashMap<Integer, String> fareList, HashSet<BusInformation> busInfo) {
        this.companyName = CompanyName;
        this.busStopages = busStopages;
        this.fareList = fareList;
        this.busInfo = busInfo;
    }

    public CompanyList(String CompanyName, HashMap<Integer, String> busStopages, HashMap<Integer, String> fareList) {
        this.companyName = CompanyName;
        this.busStopages = busStopages;
        this.fareList = fareList;
    }

    @Override
    public String toString() {
        return "CompanyList{" +
                "CompanyName=" + companyName + '\'' +
                ", busStopages=" + busStopages +
                ", fareList=" + fareList +
                ", busInfo=" + busInfo +
                '}';
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String CompanyName) {
        this.companyName = CompanyName;
    }

    public HashMap<Integer, String> getBusStopages() {
        return busStopages;
    }

    public void setBusStopages(HashMap<Integer, String> busStopages) {
        this.busStopages = busStopages;
    }

    public HashMap<Integer, String> getFareList() {
        return fareList;
    }

    public void setFareList(HashMap<Integer, String> fareList) {
        this.fareList = fareList;
    }

    public HashSet<BusInformation> getBusInfo() {
        return busInfo;
    }

    public void setBusInfo(HashSet<BusInformation> busInfo) {
        this.busInfo = busInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CompanyList CompanyList = (CompanyList) o;
        return Objects.equals(companyName, CompanyList.companyName) && Objects.equals(busStopages, CompanyList.busStopages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(companyName, busStopages);
    }
}
