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
import java.util.HashSet;
import java.util.Set;

public class Signup {
	private static Set<Event> events = new HashSet<>();
	private static Set<Union> unions = new HashSet<>();

	private final User user;
	private final Contender contender;

	public Signup(String email, String firstname, String lastname, Boolean gender, Date birthdate, String unionID, String eventTypeId, Date eventDate) {
		user = new User(email, firstname, lastname, "DTU", gender, birthdate);
		if(unionID != null || eventTypeId != null || eventDate != null) {
			Union union = new Union(unionID, unionID, "help@" + unionID + ".dk", unionID + "vej", "112");
			// this shit runs bad, but fuck it
			if (unions.contains(union)) {
				for (Union u2 : unions) {
					if (u2.equals(union)) {
						union = u2;
						break;
					}
				}
			}
			else {
				unions.add(union);
			}

			Event event = new Event(null, eventDate, union, new EventType(eventTypeId));
			// this shit runs bad, but fuck it
			if (events.contains(event)) {
				for (Event e2 : events) {
					if (e2.equals(event)) {
						event = e2;
						break;
					}
				}
			}
			else {
				events.add(event);
			}
			this.contender = new Contender(user, event);
		}
		else {
			this.contender = null;
		}
	}

	public User getUser() {
		if (this.contender != null) {
			return contender.getUser();
		}
		return user;
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
