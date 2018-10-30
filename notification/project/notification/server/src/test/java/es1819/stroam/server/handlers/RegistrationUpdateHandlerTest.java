package es1819.stroam.server.handlers;

import es1819.stroam.commons.communication.Communication;
import es1819.stroam.persistence.entities.ServiceEntity;
import es1819.stroam.persistence.entities.ServiceUserEntity;
import es1819.stroam.persistence.entities.UserEntity;
import es1819.stroam.persistence.utilities.PersistenceUtilities;
import es1819.stroam.server.Server;
import es1819.stroam.server.constants.Constants;
import es1819.stroam.server.messages.Message;
import org.json.JSONObject;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import java.util.*;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.fail;

public class RegistrationUpdateHandlerTest {

    private static EntityManager entityManager = PersistenceUtilities.getEntityManagerInstance();
    private static RegistrationUpdateHandler registrationUpdateHandler;

    /*JSONObject rightServicePayload = new JSONObject()
            .put(Constants.MESSAGE_NAME_JSON_FIELD, UUID.randomUUID().toString());*/

    JSONObject rightUserRegistrationPayload = new JSONObject()
            .put(Constants.MESSAGE_EXTERNAL_ID_JSON_FIELD, UUID.randomUUID().toString())
            .put(Constants.MESSAGE_NAME_JSON_FIELD, "TestName")
            .put(Constants.MESSAGE_EMAIL_JSON_FIELD, "Test@Test.com")
            .put(Constants.MESSAGE_PHONE_JSON_FIELD, "0123456789")
            .put(Constants.MESSAGE_PUSH_NOTIFICATION_ENABLED_JSON_FIELD, true)
            .put(Constants.MESSAGE_EMAIL_NOTIFICATION_ENABLED_JSON_FIELD, true)
            .put(Constants.MESSAGE_PHONE_NOTIFICATION_ENABLED_JSON_FIELD, true);

    JSONObject rightUserUpdatePayload = new JSONObject()
            .put(Constants.MESSAGE_EXTERNAL_ID_JSON_FIELD, UUID.randomUUID().toString())
            .put(Constants.MESSAGE_NAME_JSON_FIELD, "Test")
            .put(Constants.MESSAGE_EMAIL_JSON_FIELD, "TestMail@Test.com")
            .put(Constants.MESSAGE_PHONE_JSON_FIELD, "159875321")
            .put(Constants.MESSAGE_PUSH_NOTIFICATION_ENABLED_JSON_FIELD, false)
            .put(Constants.MESSAGE_EMAIL_NOTIFICATION_ENABLED_JSON_FIELD, false)
            .put(Constants.MESSAGE_PHONE_NOTIFICATION_ENABLED_JSON_FIELD, false);

    JSONObject rightUserUnregistrationPayload = new JSONObject()
            .put(Constants.MESSAGE_EXTERNAL_ID_JSON_FIELD, UUID.randomUUID().toString())
            .put(Constants.MESSAGE_NAME_JSON_FIELD, "Test")
            .put(Constants.MESSAGE_EMAIL_JSON_FIELD, "TestMail@Test.com")
            .put(Constants.MESSAGE_PHONE_JSON_FIELD, "159875321")
            .put(Constants.MESSAGE_PUSH_NOTIFICATION_ENABLED_JSON_FIELD, false)
            .put(Constants.MESSAGE_EMAIL_NOTIFICATION_ENABLED_JSON_FIELD, false)
            .put(Constants.MESSAGE_PHONE_NOTIFICATION_ENABLED_JSON_FIELD, false);

    JSONObject wrongPayload = new JSONObject()
            .put(Constants.MESSAGE_ID_JSON_FIELD, UUID.randomUUID().toString())
            .put(Constants.MESSAGE_NAME_JSON_FIELD, "TestName")
            .put(Constants.MESSAGE_EMAIL_JSON_FIELD, "Test@Test.com")
            .put(Constants.MESSAGE_PHONE_JSON_FIELD, "0123456789")
            .put(Constants.MESSAGE_PUSH_NOTIFICATION_ENABLED_JSON_FIELD, true)
            .put(Constants.MESSAGE_EMAIL_NOTIFICATION_ENABLED_JSON_FIELD, true)
            .put(Constants.MESSAGE_PHONE_NOTIFICATION_ENABLED_JSON_FIELD, true);

    @Test
    public void generalRightTest() {
        Communication communication = new Communication("ws://localhost:1884");
        Server server = new Server(communication);
        server.start();
        registrationUpdateHandler = new RegistrationUpdateHandler(server);
        registrationUpdateHandler.start();

        /*String serviceId = serviceRegistration();
        String userId = userRegistration(serviceId);
        associateServiceUserValidation(serviceId, userId);
        updateUser(serviceId, userId);
        userUnregistration(serviceId, userId);
        serviceUnregistration(serviceId);*/


        //Random random = new Random();
        String test = serviceRegistration();
        for (int i = 0; i < 10; i++) {
            Message userRegistrationMessage = new Message("/notTheService/" + test + "/register", rightUserRegistrationPayload.toString().getBytes());
            registrationUpdateHandler.handleMessage(userRegistrationMessage);
            System.out.println("->->-> " + i);

            //int sleep = random.nextInt(600);
            //System.out.println("Next sleep: " + sleep);
            //try { Thread.sleep(sleep); } catch (InterruptedException e) {}
        }
        try { Thread.sleep(300000); } catch (InterruptedException e) {}
        server.stop();
    }

    private String serviceRegistration() {
        System.out.println("Registering service");

        //Simulate message receive
        String generatedName = UUID.randomUUID().toString();
        JSONObject rightServicePayload = new JSONObject()
                .put(Constants.MESSAGE_NAME_JSON_FIELD, generatedName);

        Message serviceRegistrationMessage = new Message("/notTheService/register", rightServicePayload.toString().getBytes());
        registrationUpdateHandler.handleMessage(serviceRegistrationMessage);

        ServiceEntity serviceEntity = null;
        try {
            Thread.sleep(1000);
            serviceEntity = entityManager.createQuery("SELECT s FROM ServiceEntity s WHERE s.name LIKE: s_n", ServiceEntity.class)
                    .setParameter("s_n", generatedName)
                    .getSingleResult();
        } catch (NoResultException e) {
            fail("Service not registered: " + e.toString());
        } catch (InterruptedException ignored) {}

        assertNotNull(serviceEntity);

        System.out.println("Service registered with id: " + serviceEntity.getId());
        return serviceEntity.getId();
    }

    private String userRegistration(String serviceId) {
        System.out.println("Registering user");

        //Simulate message receive
        String generatedExternalId = rightUserRegistrationPayload.getString(Constants.MESSAGE_EXTERNAL_ID_JSON_FIELD); //Used to get new entity from database

        Message userRegistrationMessage = new Message("/notTheService/"+ serviceId + "/register", rightUserRegistrationPayload.toString().getBytes());
        registrationUpdateHandler.handleMessage(userRegistrationMessage);

        UserEntity userEntity = null;
        try {
            Thread.sleep(1000);
            userEntity = entityManager.createQuery("SELECT u FROM UserEntity u WHERE u.externalId LIKE: u_eid", UserEntity.class)
                    .setParameter("u_eid", generatedExternalId)
                    .getSingleResult();
        } catch (NoResultException e) {
            fail("User not registered: " + e.toString());
        } catch (InterruptedException ignored) {}

        assertNotNull(userEntity);
        assertEquals("Expected external id for user " + userEntity.getId(), rightUserRegistrationPayload.getString(Constants.MESSAGE_EXTERNAL_ID_JSON_FIELD), userEntity.getExternalId());
        assertEquals("Expected name for user " + userEntity.getId(), rightUserRegistrationPayload.getString(Constants.MESSAGE_NAME_JSON_FIELD), userEntity.getName());
        assertEquals("Expected email for user " + userEntity.getId(), rightUserRegistrationPayload.getString(Constants.MESSAGE_EMAIL_JSON_FIELD), userEntity.getEmailAddress());
        assertEquals("Expected phone for user " + userEntity.getId(), rightUserRegistrationPayload.getString(Constants.MESSAGE_PHONE_JSON_FIELD), userEntity.getPhoneNumber());
        assertEquals("Expected push notification for user " + userEntity.getId(), (byte)(rightUserRegistrationPayload.getBoolean(Constants.MESSAGE_PUSH_NOTIFICATION_ENABLED_JSON_FIELD) ? 1 : 0), userEntity.getPushNotification());
        assertEquals("Expected email notification for user " + userEntity.getId(), (byte)(rightUserRegistrationPayload.getBoolean(Constants.MESSAGE_EMAIL_NOTIFICATION_ENABLED_JSON_FIELD) ? 1 : 0), userEntity.getEmailNotification());
        assertEquals("Expected phone notification for user " + userEntity.getId(), (byte)(rightUserRegistrationPayload.getBoolean(Constants.MESSAGE_PHONE_NOTIFICATION_ENABLED_JSON_FIELD) ? 1 : 0), userEntity.getPhoneNotification());

        System.out.println("User registered with id: " + userEntity.getId());
        return userEntity.getId();
    }

    private void associateServiceUserValidation(String serviceId, String userId) {
        try {
            entityManager.createQuery("SELECT su FROM ServiceUserEntity su " +
                    "WHERE su.userId LIKE: userId AND su.serviceId LIKE: serviceId", ServiceUserEntity.class)
                    .setParameter("userId", userId)
                    .setParameter("serviceId", serviceId)
                    .getSingleResult();
        } catch (NoResultException e) {
            fail("User not associated to service: " + e.toString());
        }
    }

    private void updateUser(String serviceId, String userId) {
        System.out.println("Updating user");

        //Simulate message receive
        Message userUpdateMessage = new Message("/notTheService/"+ serviceId + "/" + userId + "/update", rightUserUpdatePayload.toString().getBytes());
        registrationUpdateHandler.handleMessage(userUpdateMessage);

        UserEntity userEntity = null;
        try {
            Thread.sleep(1000);
            userEntity = entityManager.createQuery("SELECT u FROM UserEntity u WHERE u.id LIKE: u_id", UserEntity.class)
                    .setParameter("u_id", userId)
                    .getSingleResult();
            entityManager.refresh(userEntity);
        } catch (NoResultException e) {
            fail("User not registered: " + e.toString());
        } catch (InterruptedException ignored) {}

        assertNotNull(userEntity);
        assertEquals("Expected external id update for user " + userId, rightUserUpdatePayload.getString(Constants.MESSAGE_EXTERNAL_ID_JSON_FIELD), userEntity.getExternalId());
        assertEquals("Expected name update for user " + userId, rightUserUpdatePayload.getString(Constants.MESSAGE_NAME_JSON_FIELD), userEntity.getName());
        assertEquals("Expected email update for user " + userId, rightUserUpdatePayload.getString(Constants.MESSAGE_EMAIL_JSON_FIELD), userEntity.getEmailAddress());
        assertEquals("Expected phone update for user " + userId, rightUserUpdatePayload.getString(Constants.MESSAGE_PHONE_JSON_FIELD), userEntity.getPhoneNumber());
        assertEquals("Expected push notification for user " + userId, (byte)(rightUserUpdatePayload.getBoolean(Constants.MESSAGE_PUSH_NOTIFICATION_ENABLED_JSON_FIELD) ? 1 : 0), userEntity.getPushNotification());
        assertEquals("Expected email notification for user " + userId, (byte)(rightUserUpdatePayload.getBoolean(Constants.MESSAGE_EMAIL_NOTIFICATION_ENABLED_JSON_FIELD) ? 1 : 0), userEntity.getEmailNotification());
        assertEquals("Expected phone notification for user " + userId, (byte)(rightUserUpdatePayload.getBoolean(Constants.MESSAGE_PHONE_NOTIFICATION_ENABLED_JSON_FIELD) ? 1 : 0), userEntity.getPhoneNotification());
    }

    private void userUnregistration(String serviceId, String userId) {
        System.out.println("Unregistrating user");

        //Simulate message receive
        Message userUpdateMessage = new Message("/notTheService/"+ serviceId + "/" + userId + "/unregister", rightUserUpdatePayload.toString().getBytes());
        registrationUpdateHandler.handleMessage(userUpdateMessage);

        UserEntity userEntity = null;
        try {
            Thread.sleep(1000);
            userEntity = entityManager.createQuery("SELECT u FROM UserEntity u WHERE u.id LIKE: u_id", UserEntity.class)
                    .setParameter("u_id", userId)
                    .getSingleResult();
            entityManager.refresh(userEntity);
        } catch (NoResultException e) {
            fail("User not registered: " + e.toString());
        } catch (InterruptedException ignored) {}

        assertNotNull(userEntity);
        assertEquals("Expected active update " + userId, 0, userEntity.getActive());
    }

    private void serviceUnregistration(String serviceId) {
        System.out.println("Unregistrating service");

        //Simulate message receive
        JSONObject rightServiceUnregistrationPayload = new JSONObject()
                .put(Constants.MESSAGE_ID_JSON_FIELD, serviceId);

        Message serviceRegistrationMessage = new Message("/notTheService/" + serviceId + "/unregister", rightServiceUnregistrationPayload.toString().getBytes());
        registrationUpdateHandler.handleMessage(serviceRegistrationMessage);

        ServiceEntity serviceEntity = null;
        try {
            Thread.sleep(1000);
            serviceEntity = entityManager.createQuery("SELECT s FROM ServiceEntity s WHERE s.id LIKE: s_id", ServiceEntity.class)
                    .setParameter("s_id", serviceId)
                    .getSingleResult();
        } catch (NoResultException e) {
            fail("Service not registered: " + e.toString());
        } catch (InterruptedException ignored) {}

        assertNotNull(serviceEntity);
        assertEquals("Expected active update " + serviceId, 0, serviceEntity.getActive());

        System.out.println("Service with id " + serviceEntity.getId() + " unregistered");
    }
}