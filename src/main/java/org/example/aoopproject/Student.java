package org.example.aoopproject;

import java.io.Serializable;
import java.util.Objects;

public class Student extends Users implements Serializable {

    private String ID;
    public Student(String name, String email, String password, String ID) {
        super(name, email, password);
        this.ID = ID;
    }

    public Student(String name, String email, String password) {
        super(name, email, password);
    }

    public String getID() {
        return ID;
    }
    public void setID(String ID) {
        this.ID = ID;
    }
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        Student student= (Student) o;
        //deciding based on email
        return Objects.equals(getEmail(), student.getEmail())||Objects.equals(getPassword(), student.getPassword());
//                || Objects.equals(ID, student.ID);

    }

    @Override
    public int hashCode() {
        // to match equals()
        return Objects.hash(getEmail(), getPassword());
    }

    @Override
    public String toString() {
        return "Student{" +
                "ID='" + ID + '\'' +
                '}';
    }
}
