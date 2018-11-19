package es1819.stroam.notification.server.core.message;

import es1819.stroam.notification.commons.Constants;
import org.json.JSONException;
import org.json.JSONObject;

public class MessageUtilities {

    //identical code in requestMessage
    public static String recoverMalformedMessageRequestId(byte[] messagePayload) {
        if(messagePayload == null || messagePayload.length == 0)
            return null;

        String jsonString = new String(messagePayload);
        if(jsonString.isEmpty())
            return null;

        JSONObject jsonData;
        try {
            jsonData = new JSONObject(jsonString);
        } catch (JSONException ignored) { return null; }

        String requestId = null;
        if(jsonData.has(Constants.JSON_REQUEST_ID_KEY)) {
            try {
                requestId = jsonData.getString(Constants.JSON_REQUEST_ID_KEY);
            } catch (JSONException ignored) {}
        }
        return requestId;
    }
}
