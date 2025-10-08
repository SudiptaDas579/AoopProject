package org.example.aoopproject;

import java.io.*;
import java.util.HashSet;

public class BusFileHandler extends Thread {

    public HashSet<BusList>busLists = new HashSet <BusList>();

    static class AppendableObjectOutputStream extends ObjectOutputStream {
        public AppendableObjectOutputStream(OutputStream out) throws IOException {
            super(out);
        }
        @Override
        protected void writeStreamHeader() throws IOException {
            reset();
        }
    }


    @Override
    public void run() {

        File file = new File("src/main/java/org/example/aoopproject/files/CompanyList.txt");

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

    public synchronized void updateInFile(File file,HashSet <BusList>hashSet){
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            ObjectOutputStream outputStream = file.length() == 0
                    ? new ObjectOutputStream(fileOutputStream)
                    : new BusFileHandler.AppendableObjectOutputStream(fileOutputStream);

            for (BusList A : hashSet) {
                outputStream.writeObject(A);
                System.out.println("Company lists written in file");
                outputStream.flush();
            }

            fileOutputStream.close();
            outputStream.close();


        } catch (FileNotFoundException e) {
            System.out.println("Error file not found");
        } catch (IOException e) {
            System.out.println("Error read/writing file");

        }
    }


}
