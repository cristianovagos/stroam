package es1819.stroam.server.handlers;

import es1819.stroam.server.messages.Message;

public interface Handler {

    void handleMessage(Message message);

}
