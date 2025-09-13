package org.example.aoopproject;

import java.io.Serializable;
import java.util.Objects;

public class General extends Users implements Serializable {
    private String NID;
    public General(String name, String email, String password, String NID) {
        super(name, email, password);
        this.NID = NID;
    }
    public General(String name, String email, String password) {
        super(name, email, password);
    }

    public String getNID() {
        return NID;
    }
    public void setNID(String NID) {
        this.NID = NID;
    }

    @Override
    public String toString() {
        return super.toString()+ "NID= " + NID + '\n';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof General)) return false;
        General general = (General) o;
        // you can decide uniqueness based on username, email, or NID
        return Objects.equals(getEmail(), general.getEmail())||Objects.equals(getPassword(), general.getPassword());
//                ||Objects.equals(NID, general.NID);

    }

    @Override
    public int hashCode() {
        // must match equals()
        return Objects.hash(getEmail(), getPassword());
    }
}
