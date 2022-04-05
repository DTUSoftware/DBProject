package dk.dtu.dbproject;

import java.util.Date;
import java.util.Objects;

public class Event {
    private final Date eventDate;
    private final Union union;
    private final EventType eventType;

    public Event(Date eventDate, Union union, EventType eventType) {
        this.eventDate = eventDate;
        this.union = union;
        this.eventType = eventType;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return eventDate.equals(event.eventDate) && union.equals(event.union) && eventType.equals(event.eventType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventDate, union, eventType);
    }
}
