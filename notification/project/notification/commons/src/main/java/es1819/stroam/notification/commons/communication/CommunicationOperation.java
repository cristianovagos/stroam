package es1819.stroam.notification.commons.communication;

public interface CommunicationOperation {
    void connect() throws Exception;
    void disconnect() throws Exception;
    void subscribe(String channel) throws Exception;
    void unsubscribe(String channel) throws Exception;
    boolean isConnected();
    boolean send(String channel, byte[] payload) throws Exception;
}
