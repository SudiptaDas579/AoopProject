package org.example.aoopproject;

import java.io.*;
import java.util.HashSet;


public class DatabaseFile extends Thread{

    public DatabaseFile(String Name) {
        super(Name);
    }

    HashSet<Authority> AuthorityHashSet = new HashSet<>();
    HashSet<General> GeneralHashSet = new HashSet<>();
    HashSet<Student> StudentHashSet = new HashSet<>();

    static class AppendableObjectOutputStream extends ObjectOutputStream {
        public AppendableObjectOutputStream(OutputStream out) throws IOException {
            super(out);
        }
        @Override
        protected void writeStreamHeader() throws IOException {
            reset();
        }
    }

    public void run() {
    File authority = new File("src/main/java/org/example/aoopproject/files/authority.txt");
    File general = new File("src/main/java/org/example/aoopproject/files/general.txt");
    File student = new File("src/main/java/org/example/aoopproject/files/student.txt");
    try {
        if (!authority.exists()) {
            authority.createNewFile();
        }
        if (!general.exists()) {
            general.createNewFile();
        }
        if (!student.exists()) {
            student.createNewFile();
        }
    } catch (IOException e) {
        System.out.println("Error creating file");
    }
}
    //add to file from hashset

    public synchronized void updateInFile(File file,HashSet hashSet){
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            ObjectOutputStream outputStream = file.length() == 0
                    ? new ObjectOutputStream(fileOutputStream)
                    : new AppendableObjectOutputStream(fileOutputStream);

            for (var A : hashSet) {
                outputStream.writeObject(A);
                System.out.println("Account details written in file");
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
    //add to hashset from file for general

    public HashSet<General> GeneralHashSet(){
        try {
            File file = new File("src/main/java/org/example/aoopproject/files/general.txt");
            FileInputStream fileInputStream = new FileInputStream(file);
            ObjectInputStream inputStream = new ObjectInputStream(fileInputStream);
            try {
                while (true) {
                    GeneralHashSet.add((General) inputStream.readObject());
                }
            } catch (EOFException e) {
                System.out.println("file ended");
            }
            fileInputStream.close();
            inputStream.close();


        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("Error read/writing file");
        } catch (ClassNotFoundException e) {
            System.out.println("Error class not found");
        }
        return GeneralHashSet;
    }
    // add to file for student
    public HashSet<Student> StudentHashSet(){
        try {
            File file = new File("src/main/java/org/example/aoopproject/files/student.txt");
            FileInputStream fileInputStream = new FileInputStream(file);
            ObjectInputStream inputStream = new ObjectInputStream(fileInputStream);
            try {
                while (true) {
                    StudentHashSet.add((Student) inputStream.readObject());
                }
            } catch (EOFException e) {
                System.out.println("file ended");
            }
            fileInputStream.close();
            inputStream.close();


        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("Error  read/writing file");
        } catch (ClassNotFoundException e) {
            System.out.println("Class not found");
        }
        return StudentHashSet;
    }
    // add to file for authority
    public HashSet<Authority> AuthorityHashSet(){
        try {
            File file = new File("src/main/java/org/example/aoopproject/files/authority.txt");
            FileInputStream fileInputStream = new FileInputStream(file);
            ObjectInputStream inputStream = new ObjectInputStream(fileInputStream);
            try {
                while (true) {
                    AuthorityHashSet.add((Authority) inputStream.readObject());
                }
            } catch (EOFException e) {
                System.out.println("file ended");
            }
            fileInputStream.close();
            inputStream.close();


        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("Error read/writing file");
        } catch (ClassNotFoundException e) {
            System.out.println("Class not found");
        }
        return AuthorityHashSet;
    }


}
