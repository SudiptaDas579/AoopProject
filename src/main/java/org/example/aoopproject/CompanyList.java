package org.example.aoopproject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

public class CompanyList implements Serializable {
    private String companyName;
    private HashMap<Integer,String> busStopages;
    private HashMap<Integer,String> fareList;
    private HashSet<BusInformation> busInfo;

    public CompanyList(String CompanyName, HashMap<Integer, String> busStopages, HashMap<Integer, String> fareList, HashSet<BusInformation> busInfo) {
        this.companyName = CompanyName;
        this.busStopages = busStopages;
        this.fareList = fareList;
        this.busInfo = (busInfo != null) ? busInfo : new HashSet<>();
    }

    public String busStoppageName(){
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        for (String Stppage : busStopages.values()) {
            sb.append( Stppage +" - ");
        }
        sb.delete(sb.length()-3, sb.length());
        return sb.toString();
    }

    public String fareListName(){
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        for (String fare: fareList.values()) {
            sb.append(fare +" - ");
        }
        sb.delete(sb.length()-3, sb.length());
        return sb.toString();
    }

    @Override
    public String toString() {
        return "\n Company Name: " +  companyName+"\n"+
                "\n Bus Stoppages :" + busStoppageName() +"\n"+
                "\n FareList :" + fareListName() +"\n"+
                "\n BusInfo :" + busInfo +"\n";
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompanyList that = (CompanyList) o;
        return companyName != null && that.companyName != null &&
                companyName.equalsIgnoreCase(that.companyName);
    }

    @Override
    public int hashCode() {
        return companyName == null ? 0 : companyName.toLowerCase().hashCode();
    }

}
