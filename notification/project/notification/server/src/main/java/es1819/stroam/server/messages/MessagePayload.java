package es1819.stroam.server.messages;

import es1819.stroam.server.constants.Constants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class MessagePayload {

    private byte[] pushNotificationBody;
    private Boolean pushNotification;
    private Boolean emailNotification;
    private Boolean phoneNotification;
    private String externalId;
    private String name;
    private String emailAddress;
    private String phoneNumber;
    private String phoneNotificationBody;
    private String emailNotificationSubject;
    private String emailNotificationBody;
    private UUID id;

    public MessagePayload(byte[] payload) {
        if(payload == null || payload.length == 0)
            throw new IllegalArgumentException("Payload cannot be null or empty");

        String jsonDataString = new String(payload);
        if(jsonDataString.isEmpty()) //TODO: adicionar log do tipo DEBUG com a string obtida caso o modo debug este activo
            throw new IllegalArgumentException("Empty data parsed from message payload");

        JSONObject jsonData;
        try {
            jsonData = new JSONObject(jsonDataString);
        } catch (JSONException jsonDataParseException) {
            throw new IllegalArgumentException("Payload contains an invalid json format", jsonDataParseException);
        }

        if(jsonData.length() == 0)
            throw new IllegalArgumentException("Payload contains an empty json format");

        if(jsonData.has(Constants.MESSAGE_ID_JSON_FIELD)) {
            String id = jsonData.getString(Constants.MESSAGE_ID_JSON_FIELD);

            if(id != null && !id.isEmpty()) {
                try {
                    this.id = UUID.fromString(id);
                } catch (IllegalArgumentException idParseException) {
                    throw new IllegalArgumentException("Payload contains an invalid id value", idParseException);
                }
            } else
                throw new IllegalArgumentException("Id, if present cannot be null or empty");
        }

        if(jsonData.has(Constants.MESSAGE_EXTERNAL_ID_JSON_FIELD)) {
            String externalId;
            try {
                externalId = jsonData.getString(Constants.MESSAGE_EXTERNAL_ID_JSON_FIELD);
            } catch (JSONException jsonValueException) {
                throw new IllegalArgumentException("Invalid json value parsed for " +
                        Constants.MESSAGE_EXTERNAL_ID_JSON_FIELD  + " key", jsonValueException);
            }

            if(externalId != null && !externalId.isEmpty())
                this.externalId = externalId;
        }

        if(jsonData.has(Constants.MESSAGE_NAME_JSON_FIELD)) {
            String name;
            try {
                name = jsonData.getString(Constants.MESSAGE_NAME_JSON_FIELD);
            } catch (JSONException jsonValueException) {
                throw new IllegalArgumentException("Invalid json value parsed for " +
                        Constants.MESSAGE_NAME_JSON_FIELD  + " key", jsonValueException);
            }

            if(name != null && !name.isEmpty())
                this.name = name;
        }

        if(jsonData.has(Constants.MESSAGE_EMAIL_JSON_FIELD)) {
            String emailAddress;
            try {
                emailAddress = jsonData.getString(Constants.MESSAGE_EMAIL_JSON_FIELD);
            } catch (JSONException jsonValueException) {
                throw new IllegalArgumentException("Invalid json value parsed for " +
                        Constants.MESSAGE_EMAIL_JSON_FIELD  + " key", jsonValueException);
            }

            if(emailAddress != null && !emailAddress.isEmpty())
                this.emailAddress = emailAddress;
        }

        if(jsonData.has(Constants.MESSAGE_PHONE_JSON_FIELD)) {
            String phoneNumber;
            try {
                phoneNumber = jsonData.getString(Constants.MESSAGE_PHONE_JSON_FIELD);
            } catch (JSONException jsonValueException) {
                throw new IllegalArgumentException("Invalid json value parsed for " +
                        Constants.MESSAGE_PHONE_JSON_FIELD  + " key", jsonValueException);
            }

            if(phoneNumber != null && !phoneNumber.isEmpty())
                this.phoneNumber = phoneNumber;
        }

        if(jsonData.has(Constants.MESSAGE_PUSH_NOTIFICATION_ENABLED_JSON_FIELD)) {
            try {
                this.pushNotification = jsonData.getBoolean(Constants.MESSAGE_PUSH_NOTIFICATION_ENABLED_JSON_FIELD);
            } catch (JSONException jsonValueException) {
                throw new IllegalArgumentException("Invalid json value parsed for " +
                        Constants.MESSAGE_PUSH_NOTIFICATION_ENABLED_JSON_FIELD + " key", jsonValueException);
            }
        }

        if(jsonData.has(Constants.MESSAGE_EMAIL_NOTIFICATION_ENABLED_JSON_FIELD)) {
            try {
                this.emailNotification = jsonData.getBoolean(Constants.MESSAGE_EMAIL_NOTIFICATION_ENABLED_JSON_FIELD);
            } catch (JSONException jsonValueException) {
                throw new IllegalArgumentException("Invalid json value parsed for " +
                        Constants.MESSAGE_EMAIL_NOTIFICATION_ENABLED_JSON_FIELD + " key", jsonValueException);
            }
        }

        if(jsonData.has(Constants.MESSAGE_PHONE_NOTIFICATION_ENABLED_JSON_FIELD)) {
            try {
                this.phoneNotification = jsonData.getBoolean(Constants.MESSAGE_PHONE_NOTIFICATION_ENABLED_JSON_FIELD);
            } catch (JSONException jsonValueException) {
                throw new IllegalArgumentException("Invalid json value parsed for " +
                        Constants.MESSAGE_PHONE_NOTIFICATION_ENABLED_JSON_FIELD + " key", jsonValueException);
            }
        }

        if(jsonData.has(Constants.MESSAGE_PUSH_NOTIFICATION_BODY_JSON_FIELD)) {
            JSONArray pushNotificationBody;
            try {
                pushNotificationBody = jsonData.getJSONArray(Constants.MESSAGE_PUSH_NOTIFICATION_BODY_JSON_FIELD);
            } catch (JSONException jsonValueException) {
                throw new IllegalArgumentException("Invalid json value parsed for " +
                        Constants.MESSAGE_PUSH_NOTIFICATION_BODY_JSON_FIELD  + " key", jsonValueException);
            }

            if(pushNotificationBody != null && pushNotificationBody.length() > 0) {
                byte[] bytes = new byte[pushNotificationBody.length()];

                for (int i = 0; i < pushNotificationBody.length(); i++) {
                    bytes[i] = (byte)pushNotificationBody.getInt(i);
                }
                this.pushNotificationBody = bytes;
            }

        }

        if(jsonData.has(Constants.MESSAGE_EMAIL_NOTIFICATION_SUBJECT_JSON_FIELD)) {
            String emailNotificationSubject;
            try {
                emailNotificationSubject = jsonData.getString(Constants.MESSAGE_EMAIL_NOTIFICATION_SUBJECT_JSON_FIELD);
            } catch (JSONException jsonValueException) {
                throw new IllegalArgumentException("Invalid json value parsed for " +
                        Constants.MESSAGE_EMAIL_NOTIFICATION_SUBJECT_JSON_FIELD  + " key", jsonValueException);
            }

            if(emailNotificationSubject != null && !emailNotificationSubject.isEmpty())
                this.emailNotificationSubject = emailNotificationSubject;
        }

        if(jsonData.has(Constants.MESSAGE_EMAIL_NOTIFICATION_BODY_JSON_FIELD)) {
            String emailNotificationBody;
            try {
                emailNotificationBody = jsonData.getString(Constants.MESSAGE_EMAIL_NOTIFICATION_BODY_JSON_FIELD);
            } catch (JSONException jsonValueException) {
                throw new IllegalArgumentException("Invalid json value parsed for " +
                        Constants.MESSAGE_EMAIL_NOTIFICATION_BODY_JSON_FIELD  + " key", jsonValueException);
            }

            if(emailNotificationBody != null && !emailNotificationBody.isEmpty())
                this.emailNotificationBody = emailNotificationBody;
        }

        if(jsonData.has(Constants.MESSAGE_PHONE_NOTIFICATION_BODY_JSON_FIELD)) {
            String phoneNotificationBody;
            try {
                phoneNotificationBody = jsonData.getString(Constants.MESSAGE_PHONE_NOTIFICATION_BODY_JSON_FIELD);
            } catch (JSONException jsonValueException) {
                throw new IllegalArgumentException("Invalid json value parsed for " +
                        Constants.MESSAGE_PHONE_NOTIFICATION_BODY_JSON_FIELD  + " key", jsonValueException);
            }

            if(phoneNotificationBody != null && !phoneNotificationBody.isEmpty())
                this.phoneNotificationBody = phoneNotificationBody;
        }
    }

    public Boolean isPushNotificationEnabled() {
        return pushNotification;
    }

    public Boolean isEmailNotificationEnabled() {
        return emailNotification;
    }

    public Boolean isPhoneNotificationEnabled() {
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

    public byte[] getPushNotificationBody() {
        return pushNotificationBody;
    }

    public String getPhoneNotificationBody() {
        return phoneNotificationBody;
    }

    public String getEmailNotificationSubject() {
        return emailNotificationSubject;
    }

    public String getEmailNotificationBody() {
        return emailNotificationBody;
    }

    public UUID getId() {
        return id;
    }
}
