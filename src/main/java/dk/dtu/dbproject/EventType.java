package dk.dtu.dbproject;

public class EventType {
    private String eventTypeID;
    private AgeGroup ageGroup;

    public EventType(String eventTypeID) {
        this.eventTypeID = eventTypeID;
    }

    public String getEventTypeID() {
        return this.eventTypeID;
    }
}
