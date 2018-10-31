package es1819.stroam.notification.commons.communication;

import org.eclipse.paho.client.mqttv3.*;

import java.util.UUID;

public class Communication implements MqttCallback, CommunicationOperation {

    private String brokerAddress;
    private UUID clientId;
    private CommunicationCallback callback;
    private MqttClient client;

    public Communication(String brokerAddress) {
        if(brokerAddress == null || brokerAddress.isEmpty())
            throw new IllegalArgumentException("brokerAddress cannot be a null or empty value");
        this.brokerAddress = brokerAddress;

        clientId = UUID.randomUUID();
    }

    public Communication setCallback(CommunicationCallback callback) {
        this.callback = callback;
        return this;
    }

    public boolean isConnected() {
        if(client == null)
            return false;

        return client.isConnected();
    }

    public void connect() throws Exception {
        if(client != null && client.isConnected())
            return;

        if(client == null) {
            client = new MqttClient(brokerAddress, clientId.toString());
            client.setCallback(this);
        }
        client.connect();
    }

    public void disconnect() throws Exception {
        if(client == null || !client.isConnected())
            return;

        client.disconnect();
    }

    public void subscribe(String channel) throws Exception {
        if(client != null && channel != null && !channel.isEmpty())
            client.subscribe(channel);
    }

    public void unsubscribe(String channel) throws Exception {
        if(client != null && channel != null && !channel.isEmpty())
            client.unsubscribe(channel);
    }

    public synchronized boolean send(String channel, byte[] messageBytes) throws Exception {
        if(client == null || !client.isConnected() && messageBytes == null || messageBytes.length == 0)
            return false;

        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setPayload(messageBytes);
        client.publish(channel, mqttMessage);
        return true;
    }

    public void connectionLost(Throwable throwable) {
        if(callback != null)
            callback.connectionLost(throwable);
    }

    public void messageArrived(String channel, MqttMessage mqttMessage) {
        if(mqttMessage == null)
            return;

        byte[] messageBytes = mqttMessage.getPayload();
        if(messageBytes.length > 0)
            callback.messageArrived(channel, messageBytes);
    }

    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        if(iMqttDeliveryToken == null)
            return;

        MqttMessage mqttMessage = null;
        try { mqttMessage = iMqttDeliveryToken.getMessage(); } catch (MqttException ignored) {}
        if(mqttMessage == null)
            return;

        byte[] messageBytes = mqttMessage.getPayload();
        if(messageBytes.length > 0)
            callback.messageDeliveryComplete(messageBytes);
    }
}