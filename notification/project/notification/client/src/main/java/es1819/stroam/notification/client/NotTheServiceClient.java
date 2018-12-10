package es1819.stroam.notification.client;

import es1819.stroam.notification.commons.Constants;
import es1819.stroam.notification.commons.communication.mqtt.Communication;
import es1819.stroam.notification.commons.communication.mqtt.CommunicationCallback;
import es1819.stroam.notification.commons.communication.message.request.EmailRequestMessage;
import es1819.stroam.notification.commons.communication.message.request.PhoneRequestMessage;
import es1819.stroam.notification.commons.communication.message.request.RequestMessage;
import es1819.stroam.notification.commons.communication.message.response.ResponseMessage;
import es1819.stroam.notification.commons.utilities.TopicUtilities;

import java.util.Base64;

public class NotTheServiceClient {

    private Communication communication;
    private NotTheServiceClientCallback callback;

    public NotTheServiceClient(String serverAddress) {
        if(serverAddress == null || serverAddress.isEmpty())
            throw new IllegalArgumentException("serverAddress cannot be null or empty");

        this.communication = new Communication(serverAddress)
                .setCallback(new CommunicationCallbackImpl());
    }

    public NotTheServiceClient setCallback(NotTheServiceClientCallback callback) {
        this.callback = callback;
        return this;
    }

    public boolean isConnected() {
        return communication.isConnected();
    }

    public void connect() throws Exception {
        communication.connect();
        communication.subscribe(
                TopicUtilities.createTopic(
                        Constants.CHANNEL_SERVICE_PREFIX,
                        Constants.CHANNEL_REQUEST_RESPONSE_PREFIX));

        if(callback != null)
            callback.onConnect();
    }

    public void disconnect() throws Exception {
        communication.disconnect();

        if(callback != null)
            callback.onDisconnect();
    }

    public void subscribe(String topic) throws Exception {
        communication.subscribe(topic);
    }

    public void unsubscribe(String topic) throws Exception {
        communication.unsubscribe(topic);
    }

    public String sendEmail(String emailAddress, String subject, String body) throws Exception {
        return sendEmail(emailAddress, subject, body, null);
    }

    public String sendEmail(String emailAddress, String subject, String body, String requestId) throws Exception {
        EmailRequestMessage emailRequestMessage;
        if(requestId != null && !requestId.isEmpty())
            emailRequestMessage = new EmailRequestMessage(emailAddress, requestId);
        else
            emailRequestMessage = new EmailRequestMessage(emailAddress);

        emailRequestMessage.setTopic(TopicUtilities.getEmailTopic(emailAddress));

        send(emailRequestMessage
                .setEmailSubject(subject)
                .setEmailBody(body));

        return emailRequestMessage.getRequestId();
    }

    public String sendPhone(String phoneNumber, String body) throws Exception {
        return sendPhone(phoneNumber, body, null);
    }

    public String sendPhone(String phoneNumber, String body, String requestId) throws Exception {
        PhoneRequestMessage phoneRequestMessage;
        if(requestId != null && !requestId.isEmpty())
            phoneRequestMessage = new PhoneRequestMessage(phoneNumber, requestId);
        else
            phoneRequestMessage = new PhoneRequestMessage(phoneNumber);

        phoneRequestMessage.setTopic(TopicUtilities.getPhoneTopic(phoneNumber));

        send(phoneRequestMessage.setPhoneBody(body));
        return phoneRequestMessage.getRequestId();
    }

    public void sendPush(String topic, String body) throws Exception {
        if(topic == null || topic.isEmpty())
            throw new IllegalArgumentException("topic cannot be null or empty");
        if(body == null || body.isEmpty())
            throw new IllegalArgumentException("body cannot be null or empty");

        communication.send(
                Constants.CHANNEL_SEPARATOR +
                        Constants.CHANNEL_SERVICE_PREFIX +
                        topic, body.getBytes());
    }

    private void send(RequestMessage requestMessage) throws Exception {
        communication.send(requestMessage.getTopic(), requestMessage.getPayload());
    }

    //Used to hide the CommunicationCallback methods
    private class CommunicationCallbackImpl implements CommunicationCallback {

        @Override
        public void messageArrived(String topic, byte[] messageBytes) {
            if(callback != null)
                if(topic.contains(Constants.CHANNEL_REQUEST_RESPONSE_PREFIX)) {
                    ResponseMessage responseMessage;
                    try {
                        responseMessage = new ResponseMessage(topic, messageBytes);
                    } catch (Exception ignored) { return; }

                    callback.onResponseRequestArrived(
                            responseMessage.getRequestId(),
                            responseMessage.getResultCode(),
                            responseMessage.getReason()
                    );
                }
                else {
                    String decodedPushBody = null;
                    try {
                        decodedPushBody = new String(Base64.getDecoder().decode(messageBytes));
                    } catch (IllegalArgumentException ignored) { }

                    callback.onPushArrived(topic, decodedPushBody);
                }
        }

        @Override
        public void messageDeliveryComplete(byte[] messageBytes) {

        }

        @Override
        public void connectionLost(Throwable throwable) {
            if(callback != null)
                callback.onConnectionLost(throwable);
        }
    }
}
