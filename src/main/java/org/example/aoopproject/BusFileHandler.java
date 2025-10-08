package org.example.aoopproject;

import java.io.*;
import java.util.HashSet;

public class BusFileHandler extends Thread {

    public HashSet<BusList> busLists = new HashSet<BusList>();

    @Override
    public void run() {


        File file = new File("src/main/java/org/example/aoopproject/files/CompanyList.txt");

        try {



        } catch (Exception e) {
            throw new RuntimeException(e);


        }
    }
    public HashSet<BusList> getBusLists(File file,HashSet<BusList> busLists) throws IOException, ClassNotFoundException {

        FileInputStream fileInputStream = new FileInputStream(file);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

        while (true){

            busLists.add((BusList)objectInputStream.readObject());


        }

    }

}

