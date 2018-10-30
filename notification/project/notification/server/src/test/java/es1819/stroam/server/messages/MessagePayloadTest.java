package es1819.stroam.server.messages;

import es1819.stroam.server.constants.Constants;
import org.json.JSONObject;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class MessagePayloadTest {

    JSONObject messagePayload_R1 = new JSONObject()
            .put(Constants.MESSAGE_ID_JSON_FIELD, "25c672bd-211b-4bfc-bdfe-95de27281098")
            .put(Constants.MESSAGE_EXTERNAL_ID_JSON_FIELD, "test123456tste654321tset")
            .put(Constants.MESSAGE_NAME_JSON_FIELD, "TestName")
            .put(Constants.MESSAGE_EMAIL_JSON_FIELD, "Test@Test.com")
            .put(Constants.MESSAGE_PHONE_JSON_FIELD, "0123456789")
            .put(Constants.MESSAGE_PUSH_NOTIFICATION_ENABLED_JSON_FIELD, true)
            .put(Constants.MESSAGE_EMAIL_NOTIFICATION_ENABLED_JSON_FIELD, true)
            .put(Constants.MESSAGE_PHONE_NOTIFICATION_ENABLED_JSON_FIELD, false);

    JSONObject messagePayload_R2 = new JSONObject()
            .put(Constants.MESSAGE_ID_JSON_FIELD, "25c672bd-211b-4bfc-bdfe-95de27281098")
            .put(Constants.MESSAGE_EXTERNAL_ID_JSON_FIELD, "test123456tste654321tset")
            .put(Constants.MESSAGE_NAME_JSON_FIELD, "TestName")
            .put(Constants.MESSAGE_EMAIL_JSON_FIELD, "Test@Test.com")
            .put(Constants.MESSAGE_PHONE_JSON_FIELD, "0123456789")
            .put(Constants.MESSAGE_PUSH_NOTIFICATION_ENABLED_JSON_FIELD, true)
            .put(Constants.MESSAGE_EMAIL_NOTIFICATION_ENABLED_JSON_FIELD, true);

    JSONObject messagePayload_R3 = new JSONObject()
            .put(Constants.MESSAGE_ID_JSON_FIELD, "25c672bd-211b-4bfc-bdfe-95de27281098")
            .put(Constants.MESSAGE_EXTERNAL_ID_JSON_FIELD, "test123456tste654321tset")
            .put(Constants.MESSAGE_NAME_JSON_FIELD, "TestName")
            .put(Constants.MESSAGE_EMAIL_JSON_FIELD, "Test@Test.com")
            .put(Constants.MESSAGE_PHONE_JSON_FIELD, "0123456789")
            .put(Constants.MESSAGE_PUSH_NOTIFICATION_ENABLED_JSON_FIELD, true);

    JSONObject messagePayload_R4 = new JSONObject()
            .put(Constants.MESSAGE_ID_JSON_FIELD, "25c672bd-211b-4bfc-bdfe-95de27281098")
            .put(Constants.MESSAGE_EXTERNAL_ID_JSON_FIELD, "test123456tste654321tset")
            .put(Constants.MESSAGE_NAME_JSON_FIELD, "TestName")
            .put(Constants.MESSAGE_EMAIL_JSON_FIELD, "Test@Test.com")
            .put(Constants.MESSAGE_PHONE_JSON_FIELD, "0123456789");

    JSONObject messagePayload_R5 = new JSONObject()
            .put(Constants.MESSAGE_ID_JSON_FIELD, "25c672bd-211b-4bfc-bdfe-95de27281098")
            .put(Constants.MESSAGE_EXTERNAL_ID_JSON_FIELD, "test123456tste654321tset")
            .put(Constants.MESSAGE_NAME_JSON_FIELD, "TestName")
            .put(Constants.MESSAGE_EMAIL_JSON_FIELD, "Test@Test.com");

    JSONObject messagePayload_R6 = new JSONObject()
            .put(Constants.MESSAGE_ID_JSON_FIELD, "25c672bd-211b-4bfc-bdfe-95de27281098")
            .put(Constants.MESSAGE_EXTERNAL_ID_JSON_FIELD, "test123456tste654321tset")
            .put(Constants.MESSAGE_NAME_JSON_FIELD, "TestName");

    JSONObject messagePayload_R7 = new JSONObject()
            .put(Constants.MESSAGE_ID_JSON_FIELD, "25c672bd-211b-4bfc-bdfe-95de27281098")
            .put(Constants.MESSAGE_EXTERNAL_ID_JSON_FIELD, "test123456tste654321tset");

    JSONObject messagePayload_R8 = new JSONObject()
            .put(Constants.MESSAGE_ID_JSON_FIELD, "25c672bd-211b-4bfc-bdfe-95de27281098");

    JSONObject messagePayload_W1 = new JSONObject() //empty id
            .put(Constants.MESSAGE_ID_JSON_FIELD, "")
            .put(Constants.MESSAGE_EXTERNAL_ID_JSON_FIELD, "test123456tste654321tset")
            .put(Constants.MESSAGE_NAME_JSON_FIELD, "TestName")
            .put(Constants.MESSAGE_EMAIL_JSON_FIELD, "Test@Test.com")
            .put(Constants.MESSAGE_PHONE_JSON_FIELD, "0123456789")
            .put(Constants.MESSAGE_PUSH_NOTIFICATION_ENABLED_JSON_FIELD, true)
            .put(Constants.MESSAGE_EMAIL_NOTIFICATION_ENABLED_JSON_FIELD, true)
            .put(Constants.MESSAGE_PHONE_NOTIFICATION_ENABLED_JSON_FIELD, false);

    JSONObject messagePayload_W2 = new JSONObject() //malformed id
            .put(Constants.MESSAGE_ID_JSON_FIELD, "25c672bd-211b-4bfc")
            .put(Constants.MESSAGE_EXTERNAL_ID_JSON_FIELD, "test123456tste654321tset")
            .put(Constants.MESSAGE_NAME_JSON_FIELD, "TestName")
            .put(Constants.MESSAGE_EMAIL_JSON_FIELD, "Test@Test.com")
            .put(Constants.MESSAGE_PHONE_JSON_FIELD, "0123456789")
            .put(Constants.MESSAGE_PUSH_NOTIFICATION_ENABLED_JSON_FIELD, true)
            .put(Constants.MESSAGE_EMAIL_NOTIFICATION_ENABLED_JSON_FIELD, true)
            .put(Constants.MESSAGE_PHONE_NOTIFICATION_ENABLED_JSON_FIELD, true);

    JSONObject messagePayload_W3 = new JSONObject() //invalid notification values
            .put(Constants.MESSAGE_ID_JSON_FIELD, "25c672bd-211b-4bfc-bdfe-95de27281098")
            .put(Constants.MESSAGE_EXTERNAL_ID_JSON_FIELD, "test123456tste654321tset")
            .put(Constants.MESSAGE_NAME_JSON_FIELD, "TestName")
            .put(Constants.MESSAGE_EMAIL_JSON_FIELD, "Test@Test.com")
            .put(Constants.MESSAGE_PHONE_JSON_FIELD, "0123456789")
            .put(Constants.MESSAGE_PUSH_NOTIFICATION_ENABLED_JSON_FIELD, true)
            .put(Constants.MESSAGE_EMAIL_NOTIFICATION_ENABLED_JSON_FIELD, "test")
            .put(Constants.MESSAGE_PHONE_NOTIFICATION_ENABLED_JSON_FIELD, "false");

    JSONObject messagePayload_W4 = new JSONObject() //extenal id is a string array
            .put(Constants.MESSAGE_ID_JSON_FIELD, "25c672bd-211b-4bfc-bdfe-95de27281098")
            .put(Constants.MESSAGE_EXTERNAL_ID_JSON_FIELD, new String[] { "test123456tste654321tset", "test123456tste654321tset1"})
            .put(Constants.MESSAGE_NAME_JSON_FIELD, "TestName")
            .put(Constants.MESSAGE_EMAIL_JSON_FIELD, "Test@Test.com")
            .put(Constants.MESSAGE_PHONE_JSON_FIELD, "0123456789")
            .put(Constants.MESSAGE_PUSH_NOTIFICATION_ENABLED_JSON_FIELD, true)
            .put(Constants.MESSAGE_EMAIL_NOTIFICATION_ENABLED_JSON_FIELD, true)
            .put(Constants.MESSAGE_PHONE_NOTIFICATION_ENABLED_JSON_FIELD, true);

    JSONObject messagePayload_W5 = new JSONObject();

    //messagePayload_W6 -> empty byte array

    @Test
    public void generalMessageParse() {
        new MessagePayload(messagePayload_R1.toString().getBytes());
        new MessagePayload(messagePayload_R2.toString().getBytes());
        new MessagePayload(messagePayload_R3.toString().getBytes());
        new MessagePayload(messagePayload_R4.toString().getBytes());
        new MessagePayload(messagePayload_R5.toString().getBytes());
        new MessagePayload(messagePayload_R6.toString().getBytes());
        new MessagePayload(messagePayload_R7.toString().getBytes());
        new MessagePayload(messagePayload_R8.toString().getBytes());

        try {
            new MessagePayload(messagePayload_W1.toString().getBytes());
            fail("Malformed message payload was created");
        } catch (IllegalArgumentException e) {
            System.out.println("Malformed message 1:" + e.toString());
        }

        try {
            new MessagePayload(messagePayload_W2.toString().getBytes());
            fail("Malformed message payload was created");
        } catch (IllegalArgumentException e) {
            System.out.println("Malformed message 2:" + e.toString());
        }

        try {
            new MessagePayload(messagePayload_W3.toString().getBytes());
            fail("Malformed message payload was created");
        } catch (IllegalArgumentException e) {
            System.out.println("Malformed message 3:" + e.toString());
        }

        try {
            new MessagePayload(messagePayload_W4.toString().getBytes());
            fail("Malformed message payload was created");
        } catch (IllegalArgumentException e) {
            System.out.println("Malformed message 4:" + e.toString());
        }

        try {
            new MessagePayload(messagePayload_W5.toString().getBytes());
            fail("Malformed message payload was created");
        } catch (IllegalArgumentException e) {
            System.out.println("Malformed message 5" + e.toString());
        }

        try {
            new MessagePayload(new byte[] {});
            fail("Malformed message payload was created");
        } catch (IllegalArgumentException e) {
            System.out.println("Malformed message 7:" + e.toString());
        }

    }

    @Test
    public void isPushNotification() {
        MessagePayload message = new MessagePayload(messagePayload_R1.toString().getBytes());
        assertEquals(true, message.isPushNotificationEnabled());
    }

    @Test
    public void isEmailNotification() {
        MessagePayload message = new MessagePayload(messagePayload_R1.toString().getBytes());
        assertEquals(true, message.isEmailNotificationEnabled());
    }

    @Test
    public void isPhoneNotification() {
        MessagePayload message = new MessagePayload(messagePayload_R1.toString().getBytes());
        assertEquals(false, message.isPhoneNotificationEnabled());
    }

    @Test
    public void getExternalId() {
        MessagePayload message = new MessagePayload(messagePayload_R1.toString().getBytes());
        assertEquals("test123456tste654321tset", message.getExternalId());
    }

    @Test
    public void getName() {
        MessagePayload message = new MessagePayload(messagePayload_R1.toString().getBytes());
        assertEquals("TestName", message.getName());
    }

    @Test
    public void getEmailAddress() {
        MessagePayload message = new MessagePayload(messagePayload_R1.toString().getBytes());
        assertEquals("Test@Test.com", message.getEmailAddress());
    }

    @Test
    public void getPhoneNumber() {
        MessagePayload message = new MessagePayload(messagePayload_R1.toString().getBytes());
        assertEquals("0123456789", message.getPhoneNumber());
    }

    @Test
    public void getId() {
        MessagePayload message = new MessagePayload(messagePayload_R1.toString().getBytes());
        assertEquals(UUID.fromString("25c672bd-211b-4bfc-bdfe-95de27281098"), message.getId());
    }
}