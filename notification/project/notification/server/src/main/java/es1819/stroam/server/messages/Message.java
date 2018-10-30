package es1819.stroam.server.messages;

import es1819.stroam.server.constants.Constants;

import java.util.UUID;

public class Message {

    private String channel;
    private MessageType type;
    private UUID serviceId;
    private UUID userId;
    private byte[] payload;

    public Message(String channel, byte[] payload) { //TODO: testar bem os canais. experimentar com partes nulas e por ai adiante
        if(channel == null || channel.isEmpty())
            throw new IllegalArgumentException("Channel cannot be null or empty");

        if(payload.length == 0)
            throw new IllegalArgumentException("Message cannot have a empty content (payload)");

        String[] channelParts = channel.split(Constants.CHANNEL_SEPARATOR);

        if(channelParts.length == 0)
            throw new IllegalArgumentException("Message contains a invalid service channel path");

        if(channelParts[0].isEmpty()) //means that channel starts with /...
            System.arraycopy(channelParts, 1, channelParts, 0, channelParts.length - 1); //remove the null string from the beginning

        if(!channelParts[0].equals(Constants.CHANNEL_SERVICE_PREFIX))
            throw new IllegalArgumentException("Message contains a invalid service channel path");

        this.channel = channel;
        this.payload = payload;

        //Received message for service registration
        if(channelParts[1].equals(Constants.CHANNEL_REGISTER_SUFFIX)) {
            this.type = MessageType.SERVICE_REGISTRATION;
            return; //Message parse is completed according with the type of the message
        }

        //Received message with service operation
        try{
            this.serviceId = UUID.fromString(channelParts[1]);
        } catch (IllegalArgumentException serviceIdParseException) {
        throw new IllegalArgumentException("Message contains a invalid service id in the channel path",
                serviceIdParseException);
        }

        //Service user registration/unregistration
        if(channelParts[2].equals(Constants.CHANNEL_REGISTER_SUFFIX)) {
            this.type = MessageType.SERVICE_USER_REGISTRATION;
            return; //Message parse is completed according with the type of the message
        } else if(channelParts[2].equals(Constants.CHANNEL_UNREGISTER_SUFFIX)) {
            this.type = MessageType.SERVICE_UNREGISTRATION;
            return; //Message parse is completed according with the type of the message
        }

        //User operation message
        try {
            this.userId = UUID.fromString(channelParts[2]);
        } catch (IllegalArgumentException userIdParseException) {
            throw new IllegalArgumentException("Message contains a invalid user id in the channel path",
                    userIdParseException);
        }

        //Set the service operation
        if(channelParts[3].equals(Constants.CHANNEL_UNREGISTER_SUFFIX)) //User unregistration
            this.type = MessageType.SERVICE_USER_UNREGISTRATION;
        else if(channelParts[3].equals(Constants.CHANNEL_UPDATE_SUFFIX)) //User update
            this.type = MessageType.SERVICE_USER_UPDATE;
        else if(channelParts[3].equals(Constants.CHANNEL_PUSH_SUFFIX)) //User push notification
            this.type = MessageType.USER_PUSH;
        else if(channelParts[3].equals(Constants.CHANNEL_EMAIL_SUFFIX)) //User email notification
            this.type = MessageType.USER_EMAIL;
        else if(channelParts[3].equals(Constants.CHANNEL_PHONE_SUFFIX)) //User phone notification
            this.type = MessageType.USER_PHONE;
        else throw new IllegalArgumentException("Message contains a invalid user operation in the channel path");
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

