package dk.dtu.dbproject;

import com.google.common.io.Resources;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Main function for the DB project
 */
public class Main {
    public static void main(String[] args) {
        IndlaesPersonerOgTilmeldinger laeser = new IndlaesPersonerOgTilmeldinger();
        try {
            List<PersonOgTilmelding> personerOgTilmeldinger = laeser.indlaesPersonerOgTilmeldinger(Resources.class.getClassLoader().getResource("tilmeldinger.csv").getPath());
            for(PersonOgTilmelding personOgTilmelding : personerOgTilmeldinger) {
                System.out.print("Person: " +personOgTilmelding.getPerson());
                if(personOgTilmelding.getTilmelding() != null)
                    System.out.println("\tTilmelding: " +personOgTilmelding.getTilmelding());
                else
                    System.out.println("\t Ingen tilhørende tilmelding");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Database db = new Database("localhost", 3306, "root", "");
        if (!db.connected()) {
            return;
        }
    }
}
