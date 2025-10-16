package org.example.aoopproject;

import java.io.*;
import java.util.HashSet;

public class BusFileHandler {

    public HashSet<CompanyList> companyLists = new HashSet<>();

    public HashSet<CompanyList> getCompanyLists(File file) {
        companyLists.clear();

        try {
            if (!file.exists()) {
                file.createNewFile();
                return companyLists;
            }

            try (FileInputStream fis = new FileInputStream(file);
                 ObjectInputStream ois = new ObjectInputStream(fis)) {

                while (true) {
                    try {
                        CompanyList company = (CompanyList) ois.readObject();
                        companyLists.add(company);
                    } catch (EOFException e) {
                        break;
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        return companyLists;
    }

    public synchronized void updateInFile(File file, HashSet<CompanyList> hashSet) {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            try (FileOutputStream fos = new FileOutputStream(file, false);
                 ObjectOutputStream oos = new ObjectOutputStream(fos)) {

                for (CompanyList company : hashSet) {
                    oos.writeObject(company);
                }
                oos.flush();
                System.out.println("Company lists written to file successfully.");
            }

        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
        }
    }
}
