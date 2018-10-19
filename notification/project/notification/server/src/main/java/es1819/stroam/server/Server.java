package es1819.stroam.server;

import es1819.stroam.commons.communication.Communication;
import es1819.stroam.commons.communication.CommunicationCallback;
import es1819.stroam.persistence.utilities.PersistenceUtilities;
import es1819.stroam.server.utilities.GeneralUtilities;

import javax.persistence.EntityManager;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class Server implements Runnable, CommunicationCallback {

    private boolean keepRunning;
    private Communication communication;
    private Queue<MessageTopicBytes> receivedMessagesQueue = new LinkedList<>();
    private Semaphore receivedMessagesQueueAccessControllerMutex = new Semaphore(1);
    private Semaphore threadRunController = new Semaphore(0);

    public Server(Communication communication) { //TODO: ver se mantem Handler messageHandler) {
        if(communication == null)
            throw new IllegalArgumentException("communication cannot be null");

        this.communication = communication;
        communication.setCallback(this);
    }

    public boolean start() {
        if(keepRunning)
            return false;

        if(!communication.isConnected()) {
            try {
                communication.connect();
            } catch (Exception communicationConnectException) {
                communicationConnectException.printStackTrace(); //TODO: colocar stacktrace no log
                System.out.println("Connection failed. Cause: "
                        + communicationConnectException.getMessage() + "\n"
                        + Strings.SEE_LOG_REGISTERY);
                System.out.println("Server start aborted!");
                return false; //abort the server start
            }
        }

        //before server start procedures
        try {
            subscribeDatabaseChannels();
        } catch (Exception subscribeDatabaseChannelsException) {
            subscribeDatabaseChannelsException.printStackTrace();//TODO: colocar stacktrace no log
            System.out.println("Server start failed. Cause: "
                    + subscribeDatabaseChannelsException.getMessage() + "\n"
                    + Strings.SEE_LOG_REGISTERY);
            System.out.println("Server start aborted!");
            return false; //abort the server start
        }

        //start the server
        keepRunning = true;
        new Thread(this).start();
        return true;
    }

    public void stop() {
        if(!keepRunning)
            return;

        keepRunning = false;
        communication.disconnect();
        threadRunController.release(); //release the thread to finish it
    }

    public void run() {
        while (true) {
            try { threadRunController.acquire(); } catch (InterruptedException ignored) {}

            if(!keepRunning)
                break;

            MessageTopicBytes receivedMessage = getQueuedReceivedMessage();
            System.out.println(receivedMessage.getTopic() + ":\n" + new String(receivedMessage.getBytes())); //TODO: debug
        }
    }

    public void messageArrived(String topic, byte[] messageBytes) { //this method should only enqueue received messages
        try { receivedMessagesQueueAccessControllerMutex.acquire(); } catch (InterruptedException ignored) {}
        receivedMessagesQueue.offer(new MessageTopicBytes(topic, messageBytes));
        receivedMessagesQueueAccessControllerMutex.release();

        threadRunController.release();
    }

    public void messageDeliveryComplete(byte[] messageBytes) {

    }

    public void connectionLost(Throwable throwable) {

    }

    private void subscribeDatabaseChannels() throws Exception {
        //Subscribe the channel to register new services /notTheService/register
        subscribeChannelAndLog(Constants.SERVICE_CHANNEL_PREFIX + Constants.REGISTER_CHANNEL_SUFFIX);

        EntityManager entityManager = PersistenceUtilities.getEntityManagerInstance();
        if(entityManager == null) {
            throw new NullPointerException("Entity Manager reference is null");
        }

        //Subscribe the services channels /notTheService/<serviceId>/...
        List<String> serviceIdEntries = entityManager
                .createQuery("SELECT s.id FROM ServiceEntity AS s WHERE s.active = 1", String.class)
                .getResultList();
        if(serviceIdEntries == null || serviceIdEntries.isEmpty()) {
            System.err.println("No registered services");
            return;
        }

        for (String serviceIdEntry : serviceIdEntries) {
            String serviceChannelBase = GeneralUtilities.createString(
                    Constants.SERVICE_CHANNEL_PREFIX, "/", serviceIdEntry);
            subscribeChannelAndLog(serviceChannelBase + Constants.REGISTER_CHANNEL_SUFFIX); // .../register
            subscribeChannelAndLog(serviceChannelBase + Constants.UNREGISTER_CHANNEL_SUFFIX);// .../unregister
        }

        //Subscribe the all the services users channels /notTheService/<serviceId>/<userId>/...
        List<Object[]> serviceIdUserIdEntries = PersistenceUtilities.getEntityManagerInstance()
                .createQuery("SELECT s.id AS ServiceId, u.id AS UserId FROM UserEntity AS u "
                        + "INNER JOIN ServiceUserEntity AS s_u ON u.id = s_u.userId "
                        + "INNER JOIN ServiceEntity AS s ON s_u.serviceId = s.id "
                        + "WHERE s.active = 1 AND u.active = 1", Object[].class).getResultList();
        if (serviceIdUserIdEntries == null || serviceIdUserIdEntries.isEmpty()) {
            System.err.println("No registered users");
            return;
        }

        for (Object[] serviceIdUserIdEntry : serviceIdUserIdEntries) {
            String userChannelBase = GeneralUtilities.createString(
                    Constants.SERVICE_CHANNEL_PREFIX, "/", (String) serviceIdUserIdEntry[0],
                    "/", (String) serviceIdUserIdEntry[1]);

            for (int i = 0; i < Constants.ALL_USERS_CHANNELS_SUFFIX_ARRAY.length; i++) {
                subscribeChannelAndLog(userChannelBase
                        + Constants.ALL_USERS_CHANNELS_SUFFIX_ARRAY[i]); // .../<all user suffixes>
            }
        }
    }

    private void subscribeChannelAndLog(String channel) throws Exception {
        communication.subscribe(channel); //TODO: colocar canal subscrito log
        System.out.println("Available: " + channel); //TODO: debug
    }

    private MessageTopicBytes getQueuedReceivedMessage() {
        try { receivedMessagesQueueAccessControllerMutex.acquire(); } catch (InterruptedException ignored) {}
        MessageTopicBytes receivedMessage = receivedMessagesQueue.poll();
        receivedMessagesQueueAccessControllerMutex.release();
        return receivedMessage;
    }

    private class MessageTopicBytes {
        private String topic;
        private byte[] bytes;

        MessageTopicBytes(String topic, byte[] bytes) {
            this.topic = topic;
            this.bytes = bytes;
        }

        String getTopic() {
            return topic;
        }

        byte[] getBytes() {
            return bytes;
        }
    }
}
