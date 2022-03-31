package dk.dtu.dbproject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class User {
    private final String email;
    private final String firstname;
    private final String lastname;
    private final String gender;
    private final Date birthdate;

    public User(String email, String firstname, String lastname, String gender, Date birthdate) {
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
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

    public String getGender() {
        return gender;
    }

    public Date getBirthdate() {
        return birthdate;
    }



    @Override
    public String toString() {
        final String D = ";";
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");

        return getEmail() +D + getFirstname() +D + getLastname() +D + getGender() +D +dateFormatter.format(getBirthdate());
    }
}

