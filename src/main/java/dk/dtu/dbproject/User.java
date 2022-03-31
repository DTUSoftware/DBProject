package dk.dtu.dbproject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class User {
    private final String email;
    private final String firstname;
    private final String lastname;
    private final String address;
    private final Boolean gender;
    private final Date birthdate;

    public User(String email, String firstname, String lastname, String address, Boolean gender, Date birthdate) {
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.address = address;
        this.gender = gender;
        this.birthdate = birthdate;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getAddress() {
        return address;
    }

    public Boolean getGender() {
        return gender;
    }

    public String getGenderString() {
        return (gender == null) ? "O" : (gender ? "M" : "F");
    }

    public Date getBirthdate() {
        return birthdate;
    }

    @Override
    public String toString() {
        final String D = ";";
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");

        return getEmail() +D + getFirstname() +D + getLastname() +D + getGenderString() +D +dateFormatter.format(getBirthdate());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return email.equals(user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}

