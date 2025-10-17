package org.example.aoopproject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class EventFileHandler {
    private Map<LocalDate, List<String>> events = new HashMap<>();

    public synchronized void updateInFile(File file, HashMap<LocalDate, List<String>> events) {
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
