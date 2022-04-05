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

        Database db = new Database(DATABASE_HOST, DATABASE_PORT, DATABASE_USERNAME, DATABASE_PASSWORD);
        if (!db.connected()) {
            return;
        }

        syncSignups(db, signups);
        insertTestData(db);
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
    private static void syncSignups(Database db, List<Signup> signups) {
        if (!db.connected()) {
            return;
        }

        db.startTransaction();

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

        db.endTransaction();
    }

    private static void insertTestData(Database db) {
        if (!db.connected()) {
            return;
        }

        int[] lowerAges = {0, 10, 20, 30, 60, 80};

        EventType[] eventTypes = db.getEventTypes();

        for (int i = 0; i < lowerAges.length; i++) {
            int upperAge = 200;
            if (i+1 < lowerAges.length) {
                upperAge = lowerAges[i+1]-1;
            }
            AgeGroup ageGroup = new AgeGroup(lowerAges[i], upperAge);
            db.addAgeGroup(ageGroup);
            for (EventType eventType : eventTypes) {
                db.addEventTypeAgeGroup(eventType, ageGroup, false);
                db.addEventTypeAgeGroup(eventType, ageGroup, true);
            }
        }

        Contender[] contenders = db.getContenders();
        Random random = new Random();

        for (Contender contender : contenders) {
            if (random.nextBoolean()) {
                contender.setTime(random.nextInt((2000 - 100) + 1) + 100);
                db.updateContenderTime(contender);
            }
        }

    }
}
