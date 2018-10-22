package es1819.stroam.server;

import es1819.stroam.server.constants.Constants;

public class MessageChannelPayload {

    private String channel;
    private byte[] bytes;

    MessageChannelPayload(String channel, byte[] bytes) {
        this.channel = channel;
        this.bytes = bytes;
    }

    String getChannel() {
        return channel;
    }

    String[] getChannelParts() {
        return channel.split(Constants.CHANNEL_SEPARATOR);
    }

    byte[] getBytes() {
        return bytes;
    }

}
