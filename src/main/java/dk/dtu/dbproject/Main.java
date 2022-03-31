package dk.dtu.dbproject;

import com.google.common.io.Resources;

import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.*;

/**
 * Main function for the DB project
 */
public class Main {
    private final static String DATABASE_HOST = "localhost";
    private final static int DATABASE_PORT = 3307;
    private final static String DATABASE_USERNAME = "root";
    private final static String DATABASE_PASSWORD = "";

    public static void main(String[] args) {
        List<Signup> signups = fetchSignups();
        syncSignups(signups);
    }

    /**
     * Fetch signups from the CSV file in resources.
     * @return the signups, parsed as {@link Signup}
     */
    private static List<Signup> fetchSignups() {
        CSVReader reader = new CSVReader();
        List<Signup> signups = null;
        try {
            signups = reader.readSignups(Resources.class.getClassLoader().getResource("tilmeldinger.csv").getPath());
            for(Signup signup : signups) {
                System.out.print("Person: " + signup.getUser());
                if(signup.getContender() != null)
                    System.out.println("\tTilmelding: " + signup.getContender());
                else
                    System.out.println("\t Ingen tilhørende tilmelding");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return signups;
    }

    /**
     * Syncs the signups to the database.
     */
    private static void syncSignups(List<Signup> signups) {
        Database db = new Database(DATABASE_HOST, DATABASE_PORT, DATABASE_USERNAME, DATABASE_PASSWORD);
        if (!db.connected()) {
            return;
        }

        // Ensure no duplicates
        Set<User> users = new HashSet<>();
        Set<Union> unions = new HashSet<>();
        Set<EventType> eventTypes = new HashSet<>();
        Set<Event> events = new HashSet<>();
        Set<Contender> contenders = new HashSet<>();
        for (Signup signup : signups) {
            users.add(signup.getUser());
            if (signup.getContender() != null) {
                unions.add(signup.getUnion());
                eventTypes.add(signup.getEvent().getEventType());
                events.add(signup.getEvent());
                contenders.add(signup.getContender());
            }
        }

        // Add everything
        for (User user : users) {
            db.addUser(user);
        }
        for (Union union : unions) {
            db.addUnion(union);
        }
        for (EventType eventType : eventTypes) {
            db.addEventType(eventType);
        }
        for (Event event : events) {
            db.addEvent(event);
        }
        for (Contender contender : contenders) {
            db.addContender(contender);
        }
    }
}
