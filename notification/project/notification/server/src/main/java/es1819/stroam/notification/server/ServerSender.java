package es1819.stroam.notification.server;

import es1819.stroam.notification.server.core.message.ResponseMessage;

public interface ServerSender {
    void send(ResponseMessage message) throws Exception;
}
