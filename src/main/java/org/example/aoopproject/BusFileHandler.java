package org.example.aoopproject;

import java.io.*;
import java.util.HashSet;

public class BusFileHandler extends Thread {

    public HashSet<BusList>busLists = new HashSet <BusList>();

    @Override
    public void run() {

        File file = new File("org/example/aoopproject/files/CompanyList.txt");




    }

    public HashSet<BusList> getBusLists(File file)  {


        try{

            FileInputStream fileInputStream=new FileInputStream(file);
            ObjectInputStream objectInputStream=new ObjectInputStream(fileInputStream);

            while (true) {

                busLists.add((BusList) objectInputStream.readObject());
            }
        }
        catch(Exception e){
            System.out.println("error in BusFileHandler");
        }


        return busLists;
    }

}
