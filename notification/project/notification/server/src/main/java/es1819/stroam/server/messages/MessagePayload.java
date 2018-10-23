package es1819.stroam.server.messages;

import es1819.stroam.server.constants.Constants;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class MessagePayload {

    private boolean pushNotification;
    private boolean emailNotification;
    private boolean phoneNotification;
    private String externalId;
    private String name;
    private String emailAddress;
    private String phoneNumber;
    private UUID id;

    public MessagePayload(byte[] payload) {
        if(payload == null || payload.length == 0)
            throw new IllegalArgumentException("payload cannot be null or empty");

        String jsonDataString = new String(payload);
        if(jsonDataString.isEmpty()) //TODO: adicionar log do tipo DEBUG com a string obtida caso o modo debug este activo
            throw new IllegalArgumentException("Empty data parsed from message payload");

        JSONObject jsonData;
        try {
            jsonData = new JSONObject(jsonDataString);
        } catch (JSONException jsonDataParseException) {
            throw new IllegalArgumentException("Payload contains an invalid json format", jsonDataParseException);
        }

        //TODO: metodos como .getString e .getBoolean podem dar JSONException. Nesse caso o valor do campo é ignorado caso não seja id ou externalId
        if(jsonData.has(Constants.MESSAGE_ID_JSON_FIELD)) {
            String id = jsonData.getString(Constants.MESSAGE_ID_JSON_FIELD);

            if(id != null && !id.isEmpty()) {
                try {
                    this.id = UUID.fromString(id);
                } catch (IllegalArgumentException idParseException) {
                    throw new IllegalArgumentException("Payload contains an invalid id value", idParseException);
                }
            }
        }

        if(jsonData.has(Constants.MESSAGE_EXTERNAL_ID_JSON_FIELD)) {
            String externalId = jsonData.getString(Constants.MESSAGE_EXTERNAL_ID_JSON_FIELD);

            if(externalId != null && !externalId.isEmpty())
                this.externalId = externalId;
        }

        if(jsonData.has(Constants.MESSAGE_NAME_JSON_FIELD)) {
            String name = jsonData.getString(Constants.MESSAGE_NAME_JSON_FIELD);

            if(name != null && !name.isEmpty())
                this.name = name;
        }

        if(jsonData.has(Constants.MESSAGE_EMAIL_JSON_FIELD)) {
            String emailAddress = jsonData.getString(Constants.MESSAGE_EMAIL_JSON_FIELD);

            if(emailAddress != null && !emailAddress.isEmpty())
                this.emailAddress = emailAddress;
        }

        if(jsonData.has(Constants.MESSAGE_PHONE_JSON_FIELD)) {
            String phoneNumber = jsonData.getString(Constants.MESSAGE_PHONE_JSON_FIELD);

            if(phoneNumber != null && !phoneNumber.isEmpty())
                this.phoneNumber = phoneNumber;
        }

        if(jsonData.has(Constants.MESSAGE_PUSH_NOTIFICATION_JSON_FIELD))
            this.pushNotification = jsonData.getBoolean(Constants.MESSAGE_PUSH_NOTIFICATION_JSON_FIELD);

        if(jsonData.has(Constants.MESSAGE_EMAIL_NOTIFICATION_JSON_FIELD))
            this.emailNotification = jsonData.getBoolean(Constants.MESSAGE_EMAIL_NOTIFICATION_JSON_FIELD);

        if(jsonData.has(Constants.MESSAGE_PHONE_NOTIFICATION_JSON_FIELD))
            this.phoneNotification = jsonData.getBoolean(Constants.MESSAGE_PHONE_NOTIFICATION_JSON_FIELD);
    }

    public boolean isPushNotification() {
        return pushNotification;
    }

    public boolean isEmailNotification() {
        return emailNotification;
    }

    public boolean isPhoneNotification() {
        return phoneNotification;
    }

    public String getExternalId() {
        return externalId;
    }

    public String getName() {
        return name;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public UUID getId() {
        return id;
    }
}
