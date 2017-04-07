package lightning.structby.whosup;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by azwreith on 4/5/17.
 */

public class Event {
    private String eventName;
    private String eventDescription;
    private String eventDate;
    private String eventTime;
    private String placeName;
    private double placeLat;
    private double placeLng;
    private HashMap<String, String> peopleAttending = new HashMap<>();
    public Event(String eventName, String eventDescription, String eventDate, String eventTime, String placeName, double placeLat, double placeLng, HashMap<String, String> peopleAttending) {
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.placeName = placeName;
        this.placeLat = placeLat;
        this.placeLng = placeLng;
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

    public double getPlaceLat() {
        return placeLat;
    }

    public void setPlaceLat(double placeLat) {
        this.placeLat = placeLat;
    }

    public double getPlaceLng() {
        return placeLng;
    }

    public void setPlaceLng(double placeLng) {
        this.placeLng = placeLng;
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

    public HashMap<String, String> getPeopleAttending() {
        return peopleAttending;
    }

    public void setPeopleAttending(HashMap<String, String> peopleAttending) {
        this.peopleAttending = peopleAttending;
    }

    public int getPeopleAttendingCount(){
        return peopleAttending.size();
    }

    public void addPerson(String userId){
        peopleAttending.put(userId, userId);
    }

    public void removePerson(String userId){
        peopleAttending.remove(userId);
    }
}