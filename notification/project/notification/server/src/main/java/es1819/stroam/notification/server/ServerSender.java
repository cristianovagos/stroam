package es1819.stroam.notification.server;

import es1819.stroam.notification.commons.communication.message.response.ResponseMessage;

public interface ServerSender {
    void send(ResponseMessage message) throws Exception;
}
