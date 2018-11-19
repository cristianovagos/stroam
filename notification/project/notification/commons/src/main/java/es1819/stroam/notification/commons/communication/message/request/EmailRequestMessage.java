package es1819.stroam.notification.commons.communication.message.request;

import es1819.stroam.notification.commons.Constants;
import es1819.stroam.notification.commons.utilities.TopicUtilities;
import org.json.JSONObject;

import java.util.Base64;
import java.util.UUID;

public class EmailRequestMessage extends RequestMessage {

    public EmailRequestMessage(String emailAddress) {
        super(TopicUtilities.getEmailTopic(emailAddress));

        this.requestId = UUID.randomUUID().toString();
        this.emailSubject = ""; //Avoid null exceptions
    }

    public EmailRequestMessage(String emailAddress, String requestId) {
        super(TopicUtilities.getEmailTopic(emailAddress));

        if(requestId == null || requestId.isEmpty())
            throw new IllegalArgumentException("requestId cannot be null or empty");

        this.requestId = requestId;
        this.emailSubject = ""; //Avoid null exceptions
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getEmailSubject() {
        return emailSubject;
    }

    public EmailRequestMessage setEmailSubject(String emailSubject) {
        if(emailSubject != null && !emailSubject.isEmpty())
            this.emailSubject = Base64.getEncoder().encodeToString(emailSubject.getBytes());
        return this;
    }

    public String getEmailBody() {
        return emailBody;
    }

    public EmailRequestMessage setEmailBody(String emailBody) {
        if(emailBody != null && !emailBody.isEmpty())
            this.emailBody = Base64.getEncoder().encodeToString(emailBody.getBytes());
        return this;
    }

    @Override
    public byte[] getPayload() {
        return new JSONObject()
                .put(Constants.JSON_REQUEST_ID_KEY, requestId)
                .put(Constants.JSON_EMAIL_SUBJECT_KEY, emailSubject)
                .put(Constants.JSON_EMAIL_PHONE_BODY_KEY, emailBody)
                .toString()
                .getBytes();
    }
}
