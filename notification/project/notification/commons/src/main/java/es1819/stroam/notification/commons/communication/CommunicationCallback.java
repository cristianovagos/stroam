package es1819.stroam.notification.commons.communication;

public interface CommunicationCallback {
    void messageArrived(String topic, byte[] messageBytes);
    void messageDeliveryComplete(byte[] messageBytes);
    void connectionLost(Throwable throwable);
}
