package es1819.stroam.server.messages;

import es1819.stroam.server.constants.Constants;

import java.util.UUID;

public class Message {

    private String channel;
    private MessageType type;
    private UUID serviceId;
    private UUID userId;
    private byte[] payload;

    public Message(MessageType type) {
        if(type == null)
            throw new NullPointerException("Message type cannot be null");

        this.type = type;
    }

    public static Message parse(String channel, byte[] payload) throws IllegalArgumentException {
        if(payload.length == 0)
            throw new IllegalArgumentException("Message cannot have a empty content (payload)");

        String[] channelParts = channel.split(Constants.CHANNEL_SEPARATOR);

        if(channelParts.length == 0 || channelParts[0].equals(Constants.CHANNEL_SERVICE_PREFIX))
            throw new IllegalArgumentException("Message contains a invalid service channel path");

        //Received message for service registration
        if(channelParts[1].equals(Constants.CHANNEL_REGISTER_SUFFIX))
            return new Message(MessageType.SERVICE_REGISTRATION)
                    .setChannel(channel)
                    .setPayload(payload); //Payload needs to be service id
        else if(channelParts[1].equals(Constants.CHANNEL_UNREGISTER_SUFFIX))
            return new Message(MessageType.SERVICE_UNREGISTRATION)
                    .setChannel(channel)
                    .setPayload(payload); //Payload needs to be service id

        //Received message with service operation
        UUID serviceId;
        try {
            serviceId = UUID.fromString(channelParts[1]);
        } catch (IllegalArgumentException serviceIdParseException) {
            throw new IllegalArgumentException("Message contains a invalid service id in the channel path",
                    serviceIdParseException);
        }

        //Service user registration
        if(channelParts[2].equals(Constants.CHANNEL_REGISTER_SUFFIX))
            return new Message(MessageType.SERVICE_USER_REGISTRATION)
                    .setChannel(channel)
                    .setServiceId(serviceId)
                    .setPayload(payload); //User registration message

        //User operation message
        UUID userId;
        try {
            userId = UUID.fromString(channelParts[2]);
        } catch (IllegalArgumentException userIdParseException) {
            throw new IllegalArgumentException("Message contains a invalid user id in the channel path",
                    userIdParseException);
        }

        Message temporaryMessage;
        if(channelParts[3].equals(Constants.CHANNEL_UNREGISTER_SUFFIX)) //User unregistration
            temporaryMessage = new Message(MessageType.SERVICE_USER_UNREGISTRATION);
        else if(channelParts[3].equals(Constants.CHANNEL_UPDATE_SUFFIX)) //User update
            temporaryMessage = new Message(MessageType.SERVICE_USER_UPDATE);
        else if(channelParts[3].equals(Constants.CHANNEL_PUSH_SUFFIX)) //User push notification
            temporaryMessage = new Message(MessageType.USER_PUSH);
        else if(channelParts[3].equals(Constants.CHANNEL_EMAIL_SUFFIX)) //User email notification
            temporaryMessage = new Message(MessageType.USER_EMAIL);
        else if(channelParts[3].equals(Constants.CHANNEL_PHONE_SUFFIX)) //User phone notification
            temporaryMessage = new Message(MessageType.USER_PHONE);
        else throw new IllegalArgumentException("Message contains a invalid user operation in the channel path");

        return temporaryMessage.setChannel(channel)
                .setServiceId(serviceId)
                .setUserId(userId)
                .setPayload(payload);
    }

    private Message setChannel(String channel) {
        this.channel = channel;
        return this;
    }

    private Message setServiceId(UUID serviceId) {
        this.serviceId = serviceId;
        return this;
    }

    private Message setUserId(UUID userId) {
        this.userId = userId;
        return this;
    }

    private Message setPayload(byte[] payload) {
        this.payload = payload;
        return this;
    }

    public String getChannel() {
        return channel;
    }

    public String[] getChannelParts() {
        return channel.split(Constants.CHANNEL_SEPARATOR);
    }

    public MessageType getType() {
        return type;
    }

    public UUID getServiceId() {
        return serviceId;
    }

    public UUID getUserId() {
        return userId;
    }

    public byte[] getPayload() {
        return payload;
    }
}
