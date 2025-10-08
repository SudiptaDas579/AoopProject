package org.example.aoopproject;

import java.io.*;
import java.util.HashSet;

public class BusFileHandler {

    private static final String FILE_NAME = "files/buses.txt";

    // Save all bus objects to file
    public static void saveBusLists(HashSet<BusList> busLists) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(busLists);
        }
    }

    // Load bus objects from file
    @SuppressWarnings("unchecked")
    public static HashSet<BusList> loadBusLists() throws IOException, ClassNotFoundException {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return new HashSet<>(); // return empty if no file yet
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            return (HashSet<BusList>) ois.readObject();
        }
    }
}
