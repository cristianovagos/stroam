package es1819.stroam.notification.client;

public interface NotTheServiceClientCallback {

    void onConnect();
    void onResponseRequestArrived(String requestId, int code, String reason);
    void onPushArrived(String topic, String body);
    void onConnectionLost(Throwable throwable);
    void onDisconnect();

}
