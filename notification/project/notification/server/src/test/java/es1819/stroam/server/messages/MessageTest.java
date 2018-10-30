package es1819.stroam.server.messages;

import org.junit.Test;

import static org.junit.Assert.*;

public class MessageTest {

    /*Message channel_R1 = new Message("/notTheService/register", new byte[] {1});
    Message channel_R2 = new Message("/notTheService/unregister", new byte[] {1});
    Message channel_R3 = new Message("/notTheService/3e260f74-ea58-4c48-afa0-1e1f5630f8ae/register", new byte[] {1});
    Message channel_R4 = new Message("/notTheService/3e260f74-ea58-4c48-afa0-1e1f5630f8ae/25c672bd-211b-4bfc-bdfe-95de27281098/unregister", new byte[] {1});
    Message channel_R5 = new Message("/notTheService/3e260f74-ea58-4c48-afa0-1e1f5630f8ae/25c672bd-211b-4bfc-bdfe-95de27281098/update", new byte[] {1});
    Message channel_R6 = new Message("/notTheService/3e260f74-ea58-4c48-afa0-1e1f5630f8ae/25c672bd-211b-4bfc-bdfe-95de27281098/push", new byte[] {1});
    Message channel_R7 = new Message("/notTheService/3e260f74-ea58-4c48-afa0-1e1f5630f8ae/25c672bd-211b-4bfc-bdfe-95de27281098/email", new byte[] {1});
    Message channel_R8 = new Message("/notTheService/3e260f74-ea58-4c48-afa0-1e1f5630f8ae/25c672bd-211b-4bfc-bdfe-95de27281098/notification", new byte[] {1});
    Message channel_W1 = new Message("/service/", new byte[] {1});  //no "service" as root channel and ends with separator
    Message channel_W2 = new Message("service/register", new byte[] {1}); //no "service" as root channel
    Message channel_W3 = new Message("/notTheService/3e260f74-ea58-4c48-afa0-1e1f5630f8ae/unregister", new byte[] {1}); //unregister not available
    Message channel_W4 = new Message("/notTheService/3e260f74-ea58-4c48-afa0-1e1f5630f8ae:unregister", new byte[] {1}); //no good separator
    Message channel_W5 = new Message("/notTheService/notTheService/3e260f74-ea58-4c48-afa0-1e1f5630f8ae/register", new byte[] {1}); //root repeated
    Message channel_W6 = new Message("/notTheService/3e260f74-ea58-4c48-afa0-1e1f5630f8ae/25c672bd-211b-4bfc-bdfe-95de27281098/register", new byte[] {1}); //invalid operation register
    Message channel_W7 = new Message("/notTheService/3e260f74-ea58-4c48-afa0-1e1f5630f8ae//update", new byte[] {1}); //no user id
    Message channel_W8 = new Message("3e260f74-ea58-4c48-afa0-1e1f5630f8ae//unregister", new byte[] {1}); //no root, no user id, invalid operation
    Message channel_W9 = new Message("/notTheService/3e260f74-ea58-4c48-afa0-1e1f5630f8ae/25c672bd-211b-4bfc-bdfe-95de27281098/notification", new byte[] {}); //payload length 0
    Message channel_W10 = new Message("/notTheService/3e260f74-ea58-4c48-afa0-1e1f5630f8ae/25c672bd-211b-4bfc-bdfe-95de27281098/notification", null); //payload null
    Message channel_W11 = new Message(null, new byte[] {1}); //payload null*/

    @Test
    public void generalMessageParse() {
        new Message("/notTheService/register", new byte[] {1});
        new Message("/notTheService/unregister", new byte[] {1});
        new Message("/notTheService/3e260f74-ea58-4c48-afa0-1e1f5630f8ae/register", new byte[] {1});
        new Message("/notTheService/3e260f74-ea58-4c48-afa0-1e1f5630f8ae/25c672bd-211b-4bfc-bdfe-95de27281098/unregister", new byte[] {1});
        new Message("/notTheService/3e260f74-ea58-4c48-afa0-1e1f5630f8ae/25c672bd-211b-4bfc-bdfe-95de27281098/update", new byte[] {1});
        new Message("/notTheService/3e260f74-ea58-4c48-afa0-1e1f5630f8ae/25c672bd-211b-4bfc-bdfe-95de27281098/push", new byte[] {1});
        new Message("/notTheService/3e260f74-ea58-4c48-afa0-1e1f5630f8ae/25c672bd-211b-4bfc-bdfe-95de27281098/email", new byte[] {1});
        new Message("/notTheService/3e260f74-ea58-4c48-afa0-1e1f5630f8ae/25c672bd-211b-4bfc-bdfe-95de27281098/phone", new byte[] {1});

        try {
            new Message("/service/", new byte[]{1});  //no "service" as root channel and ends with separator
            fail("Malformed message was created");
        } catch (IllegalArgumentException e) {
            System.out.println("Malformed message 1: " + e.toString());
        }

        try {
            new Message("service/register", new byte[]{1}); //no "service" as root channel
            fail("Malformed message was created");
        } catch (IllegalArgumentException e) {
            System.out.println("Malformed message 2: " + e.toString());
        }

        try {
            new Message("/notTheService/3e260f74-ea58-4c48-afa0-1e1f5630f8ae/unregister", new byte[]{1}); //unregister not available
            fail("Malformed message was created");
        } catch (IllegalArgumentException e) {
            System.out.println("Malformed message 3: " + e.toString());
        }

        try {
            new Message("/notTheService/3e260f74-ea58-4c48-afa0-1e1f5630f8ae:unregister", new byte[]{1}); //no good separator
            fail("Malformed message was created");
        } catch (IllegalArgumentException e) {
            System.out.println("Malformed message 4: " + e.toString());
        }

        try {
            new Message("/notTheService/notTheService/3e260f74-ea58-4c48-afa0-1e1f5630f8ae/register", new byte[]{1}); //root repeated
            fail("Malformed message was created");
        } catch (IllegalArgumentException e) {
            System.out.println("Malformed message 5: " + e.toString());
        }

        try {
            new Message("/notTheService/3e260f74-ea58-4c48-afa0-1e1f5630f8ae/25c672bd-211b-4bfc-bdfe-95de27281098/register", new byte[]{1}); //invalid operation register
            fail("Malformed message was created");
        } catch (IllegalArgumentException e) {
            System.out.println("Malformed message 6: " + e.toString());
        }

        try {
            new Message("/notTheService/3e260f74-ea58-4c48-afa0-1e1f5630f8ae//update", new byte[]{1}); //no user id
            fail("Malformed message was created");
        } catch (IllegalArgumentException e) {
            System.out.println("Malformed message 7:" + e.toString());
        }

        try {
            new Message("3e260f74-ea58-4c48-afa0-1e1f5630f8ae//unregister", new byte[]{1}); //no root, no user id, invalid operation
            fail("Malformed message was created");
        } catch (IllegalArgumentException e) {
            System.out.println("Malformed message 8: " + e.toString());
        }

        try {
            new Message("/notTheService/3e260f74-ea58-4c48-afa0-1e1f5630f8ae/25c672bd-211b-4bfc-bdfe-95de27281098/notification", new byte[]{}); //payload length 0
            fail("Malformed message was created");
        } catch (IllegalArgumentException e) {
            System.out.println("Malformed message 9: " + e.toString());
        }
    }

    @Test
    public void getType() {
        Message channel_R1 = new Message("/notTheService/register", new byte[] {1});
        assertEquals(MessageType.SERVICE_REGISTRATION, channel_R1.getType());

        Message channel_R2 = new Message("/notTheService/unregister", new byte[] {1});
        assertEquals(MessageType.SERVICE_UNREGISTRATION, channel_R2.getType());

        Message channel_R3 = new Message("/notTheService/3e260f74-ea58-4c48-afa0-1e1f5630f8ae/register", new byte[] {1});
        assertEquals(MessageType.SERVICE_USER_REGISTRATION, channel_R3.getType());

        Message channel_R4 = new Message("/notTheService/3e260f74-ea58-4c48-afa0-1e1f5630f8ae/25c672bd-211b-4bfc-bdfe-95de27281098/unregister", new byte[] {1});
        assertEquals(MessageType.SERVICE_USER_UNREGISTRATION, channel_R4.getType());

        Message channel_R5 = new Message("/notTheService/3e260f74-ea58-4c48-afa0-1e1f5630f8ae/25c672bd-211b-4bfc-bdfe-95de27281098/update", new byte[] {1});
        assertEquals(MessageType.SERVICE_USER_UPDATE, channel_R5.getType());

        Message channel_R6 = new Message("/notTheService/3e260f74-ea58-4c48-afa0-1e1f5630f8ae/25c672bd-211b-4bfc-bdfe-95de27281098/push", new byte[] {1});
        assertEquals(MessageType.USER_PUSH, channel_R6.getType());

        Message channel_R7 = new Message("/notTheService/3e260f74-ea58-4c48-afa0-1e1f5630f8ae/25c672bd-211b-4bfc-bdfe-95de27281098/email", new byte[] {1});
        assertEquals(MessageType.USER_EMAIL, channel_R7.getType());

        Message channel_R8 = new Message("/notTheService/3e260f74-ea58-4c48-afa0-1e1f5630f8ae/25c672bd-211b-4bfc-bdfe-95de27281098/phone", new byte[] {1});
        assertEquals(MessageType.USER_PHONE, channel_R8.getType());
    }

    @Test
    public void getServiceId() {
        Message channel_R1 = new Message("/notTheService/register", new byte[] {1});
        assertNull(channel_R1.getServiceId());

        Message channel_R2 = new Message("/notTheService/unregister", new byte[] {1});
        assertNull(channel_R2.getServiceId());

        Message channel_R3 = new Message("/notTheService/3e260f74-ea58-4c48-afa0-1e1f5630f8ae/register", new byte[] {1});
        assertEquals("3e260f74-ea58-4c48-afa0-1e1f5630f8ae", channel_R3.getServiceId().toString());

        Message channel_R4 = new Message("/notTheService/3e260f74-ea58-4c48-afa0-1e1f5630f8ae/25c672bd-211b-4bfc-bdfe-95de27281098/unregister", new byte[] {1});
        assertEquals("3e260f74-ea58-4c48-afa0-1e1f5630f8ae", channel_R4.getServiceId().toString());

        Message channel_R5 = new Message("/notTheService/3e260f74-ea58-4c48-afa0-1e1f5630f8ae/25c672bd-211b-4bfc-bdfe-95de27281098/update", new byte[] {1});
        assertEquals("3e260f74-ea58-4c48-afa0-1e1f5630f8ae", channel_R5.getServiceId().toString());

        Message channel_R6 = new Message("/notTheService/3e260f74-ea58-4c48-afa0-1e1f5630f8ae/25c672bd-211b-4bfc-bdfe-95de27281098/push", new byte[] {1});
        assertEquals("3e260f74-ea58-4c48-afa0-1e1f5630f8ae", channel_R6.getServiceId().toString());

        Message channel_R7 = new Message("/notTheService/3e260f74-ea58-4c48-afa0-1e1f5630f8ae/25c672bd-211b-4bfc-bdfe-95de27281098/email", new byte[] {1});
        assertEquals("3e260f74-ea58-4c48-afa0-1e1f5630f8ae", channel_R7.getServiceId().toString());

        Message channel_R8 = new Message("/notTheService/3e260f74-ea58-4c48-afa0-1e1f5630f8ae/25c672bd-211b-4bfc-bdfe-95de27281098/phone", new byte[] {1});
        assertEquals("3e260f74-ea58-4c48-afa0-1e1f5630f8ae", channel_R8.getServiceId().toString());
    }

    @Test
    public void getUserId() {
        Message channel_R1 = new Message("/notTheService/register", new byte[] {1});
        assertNull(channel_R1.getUserId());

        Message channel_R2 = new Message("/notTheService/unregister", new byte[] {1});
        assertNull(channel_R2.getUserId());

        Message channel_R3 = new Message("/notTheService/3e260f74-ea58-4c48-afa0-1e1f5630f8ae/register", new byte[] {1});
        assertNull(channel_R3.getUserId());

        Message channel_R4 = new Message("/notTheService/3e260f74-ea58-4c48-afa0-1e1f5630f8ae/25c672bd-211b-4bfc-bdfe-95de27281098/unregister", new byte[] {1});
        assertEquals("25c672bd-211b-4bfc-bdfe-95de27281098", channel_R4.getUserId().toString());

        Message channel_R5 = new Message("/notTheService/3e260f74-ea58-4c48-afa0-1e1f5630f8ae/25c672bd-211b-4bfc-bdfe-95de27281098/update", new byte[] {1});
        assertEquals("25c672bd-211b-4bfc-bdfe-95de27281098", channel_R5.getUserId().toString());

        Message channel_R6 = new Message("/notTheService/3e260f74-ea58-4c48-afa0-1e1f5630f8ae/25c672bd-211b-4bfc-bdfe-95de27281098/push", new byte[] {1});
        assertEquals("25c672bd-211b-4bfc-bdfe-95de27281098", channel_R6.getUserId().toString());

        Message channel_R7 = new Message("/notTheService/3e260f74-ea58-4c48-afa0-1e1f5630f8ae/25c672bd-211b-4bfc-bdfe-95de27281098/email", new byte[] {1});
        assertEquals("25c672bd-211b-4bfc-bdfe-95de27281098", channel_R7.getUserId().toString());

        Message channel_R8 = new Message("/notTheService/3e260f74-ea58-4c48-afa0-1e1f5630f8ae/25c672bd-211b-4bfc-bdfe-95de27281098/phone", new byte[] {1});
        assertEquals("25c672bd-211b-4bfc-bdfe-95de27281098", channel_R8.getUserId().toString());
    }
}