package es1819.stroam.notification.client;

import es1819.stroam.notification.commons.Constants;
import es1819.stroam.notification.commons.communication.Communication;
import es1819.stroam.notification.commons.communication.CommunicationCallback;
import org.json.JSONObject;

import java.util.Base64;
import java.util.UUID;

public class NotTheServiceClient implements CommunicationCallback {

    private Communication communication;
    private NotTheServiceClientCallback callback;

    public NotTheServiceClient(String serverAddress) {
        if(serverAddress == null || serverAddress.isEmpty())
            throw new IllegalArgumentException("serverAddress cannot be null or empty");

        this.communication = new Communication(serverAddress);
        communication.setCallback(this);
    }

    public NotTheServiceClient setCallback(NotTheServiceClientCallback callback) {
        this.callback = callback;
        return this;
    }

    public void isConnected() {
        communication.isConnected();
    }

    public void connect() throws Exception {
        communication.connect();
        communication.subscribe(
                Constants.CHANNEL_SEPARATOR +
                        Constants.CHANNEL_SERVICE_PREFIX +
                        Constants.CHANNEL_SEPARATOR +
                        Constants.CHANNEL_REQUEST_RESPONSE_PREFIX);

        if(this.callback != null)
            callback.onConnect();
    }

    public void disconnect() throws Exception {
        communication.disconnect();

        if(this.callback != null)
            callback.onDisconnect();
    }

    public void subscribe(String topic) throws Exception {
        communication.subscribe(Constants.CHANNEL_SEPARATOR + Constants.CHANNEL_SERVICE_PREFIX + topic);
    }

    public void unsubscribe(String topic) throws Exception {
        communication.unsubscribe(topic);
    }

    public String sendEmail(String emailAddress, String subject, String body) throws Exception {
        return sendEmail(emailAddress, subject, body, null);
    }

    public String sendEmail(String emailAddress, String subject, String body, String requestId) throws Exception {
        if(emailAddress == null || emailAddress.isEmpty())
            throw new IllegalArgumentException("emailAddress cannot be null or empty");

        String encodedBody;
        if(body == null || body.isEmpty())
            throw new IllegalArgumentException("body cannot be null or empty");
        else
            encodedBody = Base64.getEncoder().encodeToString(body.getBytes());

        String encodedSubject = ""; //Avoid null exceptions
        if(subject != null && !subject.isEmpty())
            encodedSubject = Base64.getEncoder().encodeToString(subject.getBytes());

        if(requestId == null)
            requestId = UUID.randomUUID().toString();

        JSONObject jsonData = new JSONObject()
                .put(Constants.JSON_REQUEST_ID_KEY, requestId)
                .put(Constants.JSON_EMAIL_SUBJECT_KEY, encodedSubject)
                .put(Constants.JSON_EMAIL_PHONE_BODY_KEY, encodedBody);

        communication.send(
                Constants.CHANNEL_SEPARATOR +
                        Constants.CHANNEL_SERVICE_PREFIX +
                        Constants.CHANNEL_SEPARATOR +
                        Constants.CHANNEL_EMAIL_PREFIX +
                        Constants.CHANNEL_SEPARATOR +
                        emailAddress,
                jsonData.toString().getBytes());
        return requestId;
    }

    public String sendPhone(String phoneNumber, String body) throws Exception {
        return sendPhone(phoneNumber, body, null);
    }

    public String sendPhone(String phoneNumber, String body, String requestId) throws Exception {
        if(phoneNumber == null || phoneNumber.isEmpty())
            throw new IllegalArgumentException("phoneNumber cannot be null or empty");

        String encodedBody;
        if(body == null || body.isEmpty())
            throw new IllegalArgumentException("body cannot be null or empty");
        else
            encodedBody = Base64.getEncoder().encodeToString(body.getBytes());

        if(requestId == null)
            requestId = UUID.randomUUID().toString();

        JSONObject jsonData = new JSONObject()
                .put(Constants.JSON_REQUEST_ID_KEY, requestId)
                .put(Constants.JSON_EMAIL_PHONE_BODY_KEY, encodedBody);

        communication.send(
                Constants.CHANNEL_SEPARATOR +
                        Constants.CHANNEL_SERVICE_PREFIX +
                        Constants.CHANNEL_SEPARATOR +
                        Constants.CHANNEL_PHONE_PREFIX +
                        Constants.CHANNEL_SEPARATOR +
                        phoneNumber,
                jsonData.toString().getBytes());
        return requestId;
    }

    public void sendPush(String topic, String body) throws Exception {
        if(topic == null || topic.isEmpty())
            throw new IllegalArgumentException("topic cannot be null or empty");
        if(body == null || body.isEmpty())
            throw new IllegalArgumentException("body cannot be null or empty");

        communication.send(Constants.CHANNEL_SEPARATOR + Constants.CHANNEL_SERVICE_PREFIX + topic, body.getBytes());
    }

    @Override
    public void messageArrived(String topic, byte[] messageBytes) {
        if(callback != null)
            if (topic.contains(Constants.CHANNEL_REQUEST_RESPONSE_PREFIX)) {
                //not implemented yet
            }
            else callback.onPushArrived(topic, new String(messageBytes));
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
