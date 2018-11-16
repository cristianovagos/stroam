package es1819.stroam.notification.server.core.message;

import es1819.stroam.notification.server.Constants;
import es1819.stroam.notification.server.core.handler.HandleResultType;
import org.json.JSONObject;

public class ResponseMessage extends Message {

    private HandleResultType handleResultType;
    private String reason = "";

    public ResponseMessage(HandleResultType handleResultType, String requestId) {
        super(MessageType.RESPONSE);

        if(handleResultType == null)
            throw new IllegalArgumentException("handleResultType cannot be null. " +
                    "Use the correct message constructor for this purpose");

        this.handleResultType = handleResultType;
        this.requestId = requestId;
    }

    public HandleResultType getHandleResultType() {
        return handleResultType;
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
                .put(Constants.JSON_RESULT_CODE_KEY, handleResultType.getResultCode())
                .put(Constants.JSON_ERROR_REASON_KEY, reason)
                .toString()
                .getBytes();
    }
}
