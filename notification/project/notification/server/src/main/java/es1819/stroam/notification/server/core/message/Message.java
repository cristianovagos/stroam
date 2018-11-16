package es1819.stroam.notification.server.core.message;

public class Message {

    protected MessageType type;
    protected String requestId;
    protected String channel;
    protected byte[] payload;

    protected Message() {
        //empty constructor (only used by heirs)
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

    public String getChannel() {
        return channel;
    }

    public Message setChannel(String channel) {
        this.channel = channel;
        return this;
    }

    public byte[] getPayload() {
        return payload;
    }
}
