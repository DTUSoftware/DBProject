package dk.dtu.dbproject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Contender {
    private final String unionID;
    private final String eventTypeID;
    private final Date eventDate;

    public Contender(String unionID, String eventTypeID, Date eventDate) {
        this.unionID = unionID;
        this.eventTypeID = eventTypeID;
        this.eventDate = eventDate;
    }

    public String getUnionID() {
        return unionID;
    }

    public String getEventTypeID() {
        return eventTypeID;
    }

    public Date getEventDate() {
        return eventDate;
    }
    @Override
    public String toString() {
        final String D = ";";
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");

        return getUnionID() +D + getEventTypeID() +D +dateFormatter.format(getEventDate());
    }

}