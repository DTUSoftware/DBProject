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
	private final Contender contender;

	public Signup(String email, String firstname, String lastname, Boolean gender, Date birthdate, String unionID, String eventTypeId, Date eventDate) {
		User user = new User(email, firstname, lastname, "DTU", gender, birthdate);
		if(unionID != null || eventTypeId != null || eventDate != null) {
			Union union = new Union(unionID, unionID, "help@" + unionID + ".dk", unionID + "vej", "112");
			Event event = new Event(null, eventDate, union, new EventType(eventTypeId));
			contender = new Contender(user, event);
		}
		else
			contender = null;
	}

	public User getUser() {
		return contender.getUser();
	}

	public Event getEvent() {
		return contender.getEvent();
	}

	public Union getUnion() {
		return contender.getEvent().getUnion();
	}

	public Contender getContender() {
		return contender;
	}
}
