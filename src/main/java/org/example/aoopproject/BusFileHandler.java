package org.example.aoopproject;

import java.io.*;
import java.util.HashSet;

public class BusFileHandler {

    public HashSet<CompanyList> getCompanyLists(File file) {
        if (!file.exists() || file.length() == 0) {
            // Return empty set if file doesn’t exist or is empty
            return new HashSet<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            // Read entire HashSet at once
            return (HashSet<CompanyList>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new HashSet<>();
        }
    }

    public synchronized void updateInFile(File file, HashSet<CompanyList> hashSet) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            // Write entire HashSet at once
            oos.writeObject(hashSet);
            oos.flush();
            System.out.println("Company lists written to file successfully.");
        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
        }
    }
}
