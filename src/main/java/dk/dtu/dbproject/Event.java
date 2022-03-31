package dk.dtu.dbproject;

import java.util.Date;

public class Event {
    private Integer eventID;
    private final Date eventDate;
    private final Union union;
    private final EventType eventType;

    public Event(Integer eventID, Date eventDate, Union union, EventType eventType) {
        this.eventID = eventID;
        this.eventDate = eventDate;
        this.union = union;
        this.eventType = eventType;
    }

    public Integer getEventID() {
        return eventID;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public Union getUnion() {
        return union;
    }

    public EventType getEventType() {
        return eventType;
    }
}
