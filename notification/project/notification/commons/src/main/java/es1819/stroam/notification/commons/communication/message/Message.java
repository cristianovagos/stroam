package es1819.stroam.notification.commons.communication.message;

import es1819.stroam.notification.commons.Constants;
import org.json.JSONException;
import org.json.JSONObject;

public class Message {

    protected MessageType type;
    protected String requestId;
    protected String topic;
    protected byte[] payload;

    protected Message() {
        //empty constructor (only used by heirs)
    }

    protected Message(String topic) {
        if(topic == null || topic.isEmpty())
            throw new IllegalArgumentException("topic cannot be null or empty");
    }

    public Message(MessageType type) {
        if(type == null)
            throw new IllegalArgumentException("messageType cannot be null");

        this.type = type;
    }

    //TODO: acrescentar apenas os setters que forem necessarios
    public MessageType getType() {
        return type;
    }

    public String getRequestId() {
        return requestId;
    }

    public Message setRequestId(String requestId) {
        this.requestId = requestId;
        return this;
    }

    public String getTopic() {
        return topic;
    }

    public Message setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public byte[] getPayload() {
        return payload;
    }
}
