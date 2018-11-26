package es1819.stroam.notification.client;

import org.junit.Test;

public class NotTheServiceClientTest implements NotTheServiceClientCallback {

    @Test
    public void generalTest() {
        NotTheServiceClient notTheServiceClient = new NotTheServiceClient("ws://localhost:1884")
                .setCallback(this);

        try {
            notTheServiceClient.connect();
            notTheServiceClient.subscribe("/test");
            /*System.out.println("email requestId: " +
                    notTheServiceClient.sendEmail("test@stroam.com", "teste", "test"));*/
            System.out.println("phone requestId: " +
                    notTheServiceClient.sendPhone("123484564", "asdojasdg"));
            notTheServiceClient.sendPush("/test", "ola");
        } catch (Exception e) {
            System.out.println("erro: ");
            e.printStackTrace();
        }

        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnect() {
        System.out.println("Connected");
    }

    @Override
    public void onResponseRequestArrived(String requestId, int code, String reason) {
        System.out.println("Response: requestId: " + requestId + " code: " + code + " reason: " + reason);
    }

    @Override
    public void onPushArrived(String topic, String body) {
        System.out.println("Push: topic: " + topic + " body: " + body);
    }

    @Override
    public void onConnectionLost(Throwable throwable) {
        System.out.println("ConnectionLost: ");
        throwable.printStackTrace();
    }

    @Override
    public void onDisconnect() {
        System.out.println("Connected");
    }
}