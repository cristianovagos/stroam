package es1819.stroam.notification.server.core.message;

import es1819.stroam.notification.server.Constants;
import org.json.JSONObject;
import org.junit.Test;

import java.util.Base64;

public class MessageTest {

    @Test
    public void messageValidationsTest() {
        JSONObject m = new JSONObject().put(Constants.JSON_REQUEST_ID_KEY, "0123456789")
                .put(Constants.JSON_EMAIL_SUBJECT_KEY, Base64.getEncoder().encode("email".getBytes()).toString())
                .put(Constants.JSON_EMAIL_PHONE_BODY_KEY, Base64.getEncoder().encode("ola pessoas".getBytes()).toString());

        JSONObject p = new JSONObject().put(Constants.JSON_REQUEST_ID_KEY, "0123456789")
                .put(Constants.JSON_EMAIL_PHONE_BODY_KEY, Base64.getEncoder().encodeToString("ola pessoas".getBytes()));

        String encoded = Base64.getEncoder().encodeToString("ola pessoas".getBytes());

        byte[] valueDecoded = Base64.getDecoder().decode(encoded);
        System.out.println("Decoded value is " + new String(valueDecoded));
    }

}