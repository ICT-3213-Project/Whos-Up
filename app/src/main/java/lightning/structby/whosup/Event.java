package lightning.structby.whosup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by azwreith on 4/5/17.
 */

public class Event {
    private String placeName;
    private String placeAddress;
    private String eventName;
    private String eventDescription;
    private String eventDate;
    private String eventTime;
    private List<String> peopleAttending = new ArrayList<>();

    public Event(String placeName, String placeAddress, String eventName, String eventDescription, String eventDate, String eventTime, List<String> peopleAttending) {
        this.placeName = placeName;
        this.placeAddress = placeAddress;
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.peopleAttending = peopleAttending;
    }

    public Event() {
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPlaceAddress() {
        return placeAddress;
    }

    public void setPlaceAddress(String placeAddress) {
        this.placeAddress = placeAddress;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public List<String> getPeopleAttending() {
        return peopleAttending;
    }

    public void setPeopleAttending(List<String> peopleAttending) {
        this.peopleAttending = peopleAttending;
    }
}