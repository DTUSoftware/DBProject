package dk.dtu.dbproject;

import com.google.common.io.Resources;

import java.io.IOException;
import java.util.List;

/**
 * Main function for the DB project
 */
public class Main {
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
        Database db = new Database("localhost", 3306, "root", "");
        if (!db.connected()) {
            return;
        }
    }
}
