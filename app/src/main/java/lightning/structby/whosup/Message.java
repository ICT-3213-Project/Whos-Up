package lightning.structby.whosup;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vinayak on 4/3/17.
 */

public class Message {

    private int eventId;
    private String senderId;
    private Date date;
    private String message;
    private String mediaUrl;

    public Message(){}

    public Message(String message, String senderId, int eventId, Date date, String mediaUrl){
        this.message = message;
        this.date = date;
        this.senderId = senderId;
        this.mediaUrl = mediaUrl;
        this.eventId = eventId;
    }


    public int getEventId() { return eventId; }

    public void setEventId(int eventId) { this.eventId = eventId; }

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
        result.put("senderId", senderId);
        result.put("mediaUrl", mediaUrl);
        result.put("message", message);
        return result;
    }


}
