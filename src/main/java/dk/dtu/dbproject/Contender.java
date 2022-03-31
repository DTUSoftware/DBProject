package dk.dtu.dbproject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contender contender = (Contender) o;
        return user.equals(contender.user) && event.equals(contender.event);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, event);
    }
}