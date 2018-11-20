package es1819.stroam.notification.commons.communication.message.response;

import es1819.stroam.notification.commons.Constants;
import es1819.stroam.notification.commons.communication.message.Message;
import es1819.stroam.notification.commons.communication.message.MessageType;
import org.json.JSONException;
import org.json.JSONObject;

public class ResponseMessage extends Message {

    private int resultCode;
    private String reason = "";

    public ResponseMessage(int resultCode, String requestId) {
        super(MessageType.RESPONSE);

        this.resultCode = resultCode;
        this.requestId = requestId;
    }

    public ResponseMessage(String topic, byte[] payload) {
        super(MessageType.RESPONSE);

        if(topic == null || topic.isEmpty())
            throw new IllegalArgumentException("topic cannot be null or empty");
        if(payload == null || payload.length == 0)
            throw new IllegalArgumentException("payload cannot be null or empty");

        this.topic = topic;
        this.payload = payload;

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
        parsePayloadResponseData(jsonData);
    }

    public int getResultCode() {
        return resultCode;
    }

    public String getReason() {
        return reason;
    }

    public ResponseMessage setReason(String reason) {
        this.reason = reason;
        return this;
    }

    @Override
    public byte[] getPayload() {
        return new JSONObject() //even null or empty, values are added in order to avoid possible problems (on the client mainly)
                .put(Constants.JSON_REQUEST_ID_KEY, requestId)
                .put(Constants.JSON_RESULT_CODE_KEY, resultCode)
                .put(Constants.JSON_REASON_KEY, reason)
                .toString()
                .getBytes();
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

    private void parsePayloadResponseData(JSONObject jsonData) {
        if(jsonData.has(Constants.JSON_RESULT_CODE_KEY)) {
            try {
                this.resultCode = jsonData.getInt(Constants.JSON_RESULT_CODE_KEY);
            } catch (JSONException jsonValueParseException) {
                throw new IllegalArgumentException("Invalid json value parsed for " +
                        Constants.JSON_RESULT_CODE_KEY  + " key", jsonValueParseException);
            }
        }

        if(jsonData.has(Constants.JSON_REASON_KEY)) {
            try {
                this.reason = jsonData.getString(Constants.JSON_REASON_KEY);
            } catch (JSONException jsonValueParseException) {
                throw new IllegalArgumentException("Invalid json value parsed for " +
                        Constants.JSON_REASON_KEY  + " key", jsonValueParseException);
            }

            if(this.reason == null)
                this.reason = ""; //not null guaranteed
        }
    }
}
