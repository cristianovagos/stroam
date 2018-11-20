package es1819.stroam.notification.commons.communication.message.request;

import es1819.stroam.notification.commons.Constants;
import es1819.stroam.notification.commons.communication.message.Message;
import es1819.stroam.notification.commons.communication.message.MessageType;
import org.apache.commons.validator.routines.EmailValidator;
import org.json.JSONException;
import org.json.JSONObject;

public class RequestMessage extends Message {

    protected String emailAddress;
    protected String emailSubject;
    protected String emailBody;
    protected String phoneNumber;
    protected String phoneBody;

    public RequestMessage(String topic) {
        super(topic);
    }

    public RequestMessage(String channel, byte[] payload) {
        super();

        if(channel == null || channel.isEmpty())
            throw new IllegalArgumentException("channel cannot be null or empty");
        if(payload == null || payload.length == 0)
            throw new IllegalArgumentException("payload cannot be null or empty");

        this.topic = channel;
        this.payload = payload;

        parseChannelData(); //Validates the channel data and defines the message type for payload validation

        String jsonString = new String(payload);
        if(jsonString.isEmpty())
            throw new IllegalArgumentException("payload parsing result in an empty string");

        JSONObject jsonData;
        try {
            jsonData = new JSONObject(jsonString);
        } catch (JSONException jsonObjectParseException) {
            throw new IllegalArgumentException("payload contains an invalid json format", jsonObjectParseException);
        }

        parsePayloadGeneralData(jsonData);

        if (this.type == MessageType.MAIL)
            parsePayloadMailData(jsonData);
        else
            parsePayloadPhoneData(jsonData);
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getEmailSubject() {
        return emailSubject;
    }

    public String getEmailBody() {
        return emailBody;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPhoneBody() {
        return phoneBody;
    }

    //parse and assign the channel values to the respective variables
    private void parseChannelData() {
        String[] channelParts = topic.split(Constants.CHANNEL_SEPARATOR);
        if(channelParts.length == 0)
            throw new IllegalArgumentException("unexpected channel format");

        if(channelParts[0].isEmpty()) { //means that channel starts with /...
            String[] temporaryChannelParts = new String[channelParts.length - 1];
            System.arraycopy(channelParts, 1, temporaryChannelParts, 0, channelParts.length - 1); //remove the null string from the beginning

            channelParts = temporaryChannelParts;
        }

        //TODO: tentar optimizar codigo abaixo
        int channelFlagIndex = -1; //search the index of the channel part email
        for (int i = 0; i < channelParts.length; i++) {
            if(channelParts[i].contentEquals(Constants.CHANNEL_EMAIL_PREFIX)) {
                this.type = MessageType.MAIL;
                channelFlagIndex = i;
            }
            else if(channelParts[i].contentEquals(Constants.CHANNEL_PHONE_PREFIX)) {
                this.type = MessageType.PHONE;
                channelFlagIndex = i;
            }
        }

        if(channelFlagIndex == -1) //type of contact not found in channel parts
            throw new IllegalArgumentException(
                    Constants.CHANNEL_EMAIL_PREFIX + " or " + Constants.CHANNEL_PHONE_PREFIX +
                            " expected in channel hierarchy");
        else if(channelFlagIndex + 1 < channelParts.length - 1) //type of contact its not at the end of channel parts
            throw new IllegalArgumentException(
                    Constants.CHANNEL_EMAIL_PREFIX + " or " + Constants.CHANNEL_PHONE_PREFIX +
                            " is expected on the penultimate part of the channel (before the contact data)");

        String contactData;
        try {
            contactData = channelParts[channelFlagIndex + 1];
        } catch (IndexOutOfBoundsException channelPartIndexOutOfBounds) {
            throw new IllegalArgumentException("contact data expected in the end of channel" +
                    " hierarchy (after contact type)", channelPartIndexOutOfBounds);
        }

        if(this.type == MessageType.MAIL) {
            if (EmailValidator.getInstance().isValid(contactData))
                this.emailAddress = contactData;
            else
                throw new IllegalArgumentException(contactData + " is not a valid email address");
        }
        else if(this.type == MessageType.PHONE) {
            this.phoneNumber = contactData; //TODO: validar numero de telefone (ver email)
        }
    }

    //parse and assign the payload general values to the respective variables
    private void parsePayloadGeneralData(JSONObject jsonData) { //not null guaranteed
        if(jsonData.has(Constants.JSON_REQUEST_ID_KEY)) {
            try {
                this.requestId = jsonData.getString(Constants.JSON_REQUEST_ID_KEY);
            } catch (JSONException jsonValueParseException) {
                throw new IllegalArgumentException("Invalid json value parsed for " +
                        Constants.JSON_REQUEST_ID_KEY  + " key", jsonValueParseException);
            }

            if(this.requestId == null)
                this.requestId = ""; //not null guaranteed
        }
    }

    //parse and assign the payload mail values to the respective variables
    private void parsePayloadMailData(JSONObject jsonData) { //not null guaranteed
        if(jsonData.has(Constants.JSON_EMAIL_SUBJECT_KEY)) {
            try {
                this.emailSubject = jsonData.getString(Constants.JSON_EMAIL_SUBJECT_KEY);
            } catch (JSONException jsonValueParseException) {
                throw new IllegalArgumentException("Invalid json value parsed for " +
                        Constants.JSON_EMAIL_SUBJECT_KEY  + " key", jsonValueParseException);
            }

            if(this.emailSubject == null)
                this.emailSubject = ""; //not null guaranteed
        }

        if(jsonData.has(Constants.JSON_EMAIL_PHONE_BODY_KEY)) {
            try {
                this.emailBody = jsonData.getString(Constants.JSON_EMAIL_PHONE_BODY_KEY);
            } catch (JSONException jsonValueParseException) {
                throw new IllegalArgumentException("Invalid json value parsed for " +
                        Constants.JSON_EMAIL_PHONE_BODY_KEY  + " key", jsonValueParseException);
            }
        }
    }

    //parse and assign the payload phone values to the respective variables
    private void parsePayloadPhoneData(JSONObject jsonData) { //not null guaranteed
        if(jsonData.has(Constants.JSON_EMAIL_PHONE_BODY_KEY)) {
            try {
                this.phoneBody = jsonData.getString(Constants.JSON_EMAIL_PHONE_BODY_KEY);
            } catch (JSONException jsonValueParseException) {
                throw new IllegalArgumentException("Invalid json value parsed for " +
                        Constants.JSON_EMAIL_PHONE_BODY_KEY  + " key", jsonValueParseException);
            }
        }
    }
}
