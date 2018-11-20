package es1819.stroam.notification.commons.communication.message.request;

import es1819.stroam.notification.commons.Constants;
import es1819.stroam.notification.commons.utilities.TopicUtilities;
import org.json.JSONObject;

import java.util.Base64;
import java.util.UUID;

public class PhoneRequestMessage extends RequestMessage {

    public PhoneRequestMessage(String phoneNumber) {
        super(TopicUtilities.getPhoneTopic(phoneNumber));

        this.requestId = UUID.randomUUID().toString();
    }

    public PhoneRequestMessage(String phoneNumber, String requestId) {
        super(TopicUtilities.getPhoneTopic(phoneNumber));

        if(requestId == null || requestId.isEmpty())
            throw new IllegalArgumentException("requestId cannot be null or empty");

        this.requestId = requestId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPhoneBody() {
        return phoneBody;
    }

    public PhoneRequestMessage setPhoneBody(String phoneBody) {
        if(phoneBody != null && !phoneBody.isEmpty())
            this.phoneBody = Base64.getEncoder().encodeToString(phoneBody.getBytes());
        return this;
    }

    @Override
    public byte[] getPayload() {
        return new JSONObject()
                .put(Constants.JSON_REQUEST_ID_KEY, requestId)
                .put(Constants.JSON_EMAIL_PHONE_BODY_KEY, phoneBody)
                .toString()
                .getBytes();
    }
}
