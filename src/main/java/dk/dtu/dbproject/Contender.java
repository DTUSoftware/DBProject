package dk.dtu.dbproject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Contender {
    private int uniqueContenderID;
    private final User user;
    private final Event event;
    private int time;

    public Contender(User user, Event event) {
        this.user = user;
        this.event = event;
    }

    public Contender(User user, Event event, int time) {
        this(user, event);
        this.time = time;
    }

    public Contender(int uniqueContenderID, User user, Event event, int time) {
        this(user, event, time);
        this.uniqueContenderID = uniqueContenderID;
    }

    public User getUser() {
        return user;
    }

    public Event getEvent() {
        return event;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    @Override
    public String toString() {
        final String D = ";";
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");

        return event.getUnion().getUnionID() +D + event.getEventType() +D +dateFormatter.format(event.getEventDate());
    }

}