package es1819.stroam.server.handlers;

import es1819.stroam.server.MessageChannelPayload;

public interface Handler {

    void handleMessage(MessageChannelPayload messageChannelPayload);

}
