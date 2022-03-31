/**
 * Denne klasse repræsenterer en Person og evt. en tilhørende tilmelding fra en datafil der modtages fra Tidsmaskinens web løsning
 * 
 * Klassen er en del af projektopgaven på Kursus 02327 F22
 * 
 * @author Thorbjørn Konstantinovitz  
 *
 */

package dk.dtu.dbproject;

import java.util.Date;

public class Signup {
	private final User user;
	private final Contender contender;

	public Signup(String email, String firstname, String lastname, String gender, Date birthdate, String unionID, String eventTypeId, Date eventDate) {
		user = new User(email, firstname, lastname, gender, birthdate);
		if(unionID != null || eventTypeId != null || eventDate != null)
			contender = new Contender(unionID, eventTypeId, eventDate);
		else
			contender = null;
	}

	public User getUser() {
		return user;
	}

	public Contender getContender() {
		return contender;
	}
}
