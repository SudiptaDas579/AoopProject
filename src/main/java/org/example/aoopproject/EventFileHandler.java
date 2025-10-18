package org.example.aoopproject;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventFileHandler {
    private HashMap<LocalDate, ArrayList<String>> events = new HashMap<>();

    public synchronized void updateInFile(File file, HashMap<LocalDate, ArrayList<String>> events) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
                FileWriter fw = new FileWriter(file, false);

                PrintWriter pw = new PrintWriter(fw);

                for (Map.Entry<LocalDate, ArrayList<String>> e : events.entrySet()) {
                    pw.println(e.getKey() + ":" + e.getValue());
                }
                pw.close();
                System.out.println("Company lists written to file successfully.");

        } catch (IOException e) {
                System.out.println("Error writing file: " + e.getMessage());
        }
    }

    public HashMap<LocalDate, ArrayList<String>> getEvents(File file) {
        events.clear();

            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return events;
            }

            try {
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);

                while (true) {
                    try {
                        String line = br.readLine();
                        if (line == null) {
                            String[] temp =line.split(":");

                            ArrayList<String> eventList= new ArrayList<>();
                            eventList.add(temp[1]);

                            events.put(LocalDate.parse(temp[0]),eventList);
                        }
                    } catch (EOFException e) {
                        break;
                    }
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        return events;
    }
}

