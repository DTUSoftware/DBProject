package dk.dtu.dbproject;

import java.util.Objects;

public class Union {
    private String unionID;
    private String name;
    private String email;
    private String address;
    private String phoneNumber;

    public Union(String unionID, String name, String email, String address, String phoneNumber) {
        this.unionID = unionID;
        this.name = name;
        this.email = email;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    public String getUnionID() {
        return unionID;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Union union = (Union) o;
        return unionID.equals(union.unionID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(unionID);
    }
}
