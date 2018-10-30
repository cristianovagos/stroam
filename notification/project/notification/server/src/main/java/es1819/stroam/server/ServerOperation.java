package es1819.stroam.server;

import es1819.stroam.server.messages.Message;

public interface ServerOperation {

    void subscribeChannels(String serviceId) throws Exception;
    void subscribeChannels(String serviceId, String userId) throws Exception;
    void unsubscribeChannels(String serviceId) throws Exception;
    void unsubscribeChannels(String serviceId, String userId) throws Exception;
    void send(String channel, byte[] payload) throws Exception;

}
