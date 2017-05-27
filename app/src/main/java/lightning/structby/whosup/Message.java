package lightning.structby.whosup;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vinayak on 4/3/17.
 */

public class Message {

    private String eventId;
    private String senderId;
    private String date;
    private String time;
    private String message;
    private String mediaUrl;

    public Message(){}

    public Message(String message, String senderId, String eventId, String date, String time, String mediaUrl){
        this.message = message;
        this.date = date;
        this.senderId = senderId;
        this.mediaUrl = mediaUrl;
        this.eventId = eventId;
        this.time = time;
    }

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }

    public void setTime(String time) { this.time = time; }

    public String getEventId() { return eventId; }

    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getSenderId() { return senderId; }

    public void setSenderId(String senderId) { this.senderId = senderId; }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }

    public String getMediaUrl() { return  mediaUrl; }

    public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("eventId", eventId);
        result.put("date", date);
        result.put("time", time);
        result.put("senderId", senderId);
        result.put("mediaUrl", mediaUrl);
        result.put("message", message);
        return result;
    }


}
