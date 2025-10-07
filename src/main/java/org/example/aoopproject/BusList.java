package org.example.aoopproject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public class BusList implements Serializable {
    private String busName;
    private HashMap<Integer,String> busStopages;
    private HashMap<Integer,Integer> fareList;
    private HashSet<BusInformation> busInfo;

    public BusList(String busName, HashMap<Integer, String> busStopages, HashMap<Integer, Integer> fareList, HashSet<BusInformation> busInfo) {
        this.busName = busName;
        this.busStopages = busStopages;
        this.fareList = fareList;
        this.busInfo = busInfo;
    }

    public BusList(String busName, HashMap<Integer, String> busStopages, HashMap<Integer, Integer> fareList) {
        this.busName = busName;
        this.busStopages = busStopages;
        this.fareList = fareList;
    }

    @Override
    public String toString() {
        return "BusList{" +
                "busName='" + busName + '\'' +
                ", busStopages=" + busStopages +
                ", fareList=" + fareList +
                ", busInfo=" + busInfo +
                '}';
    }

    public String getBusName() {
        return busName;
    }

    public void setBusName(String busName) {
        this.busName = busName;
    }

    public HashMap<Integer, String> getBusStopages() {
        return busStopages;
    }

    public void setBusStopages(HashMap<Integer, String> busStopages) {
        this.busStopages = busStopages;
    }

    public HashMap<Integer, Integer> getFareList() {
        return fareList;
    }

    public void setFareList(HashMap<Integer, Integer> fareList) {
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
        BusList busList = (BusList) o;
        return Objects.equals(busName, busList.busName) && Objects.equals(busStopages, busList.busStopages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(busName, busStopages);
    }
}
