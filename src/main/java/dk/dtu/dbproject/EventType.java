package dk.dtu.dbproject;

import java.util.Objects;

public class EventType {
    private String eventTypeID;
    private AgeGroup ageGroup;

    public EventType(String eventTypeID) {
        this.eventTypeID = eventTypeID;
    }

    public String getEventTypeID() {
        return this.eventTypeID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventType eventType = (EventType) o;
        return eventTypeID.equals(eventType.eventTypeID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventTypeID);
    }
}
