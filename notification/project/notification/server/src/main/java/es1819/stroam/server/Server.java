package es1819.stroam.server;

import es1819.stroam.commons.communication.Communication;
import es1819.stroam.commons.communication.CommunicationCallback;
import es1819.stroam.persistence.utilities.PersistenceUtilities;
import es1819.stroam.persistence.views.ProducersChannelsPathsEntity;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class Server implements Runnable, CommunicationCallback {

    private boolean keepRunning;
    private Communication communication;
    private Queue<MessageTopicBytes> receivedMessagesQueue = new LinkedList<MessageTopicBytes>();
    private Semaphore receivedMessagesQueueAccessControllerMutex = new Semaphore(1);
    private Semaphore threadRunController = new Semaphore(0);

    public Server(Communication communication) { //TODO: ver se mantem Handler messageHandler) {
        if(communication == null)
            throw new IllegalArgumentException("communication cannot be null");

        this.communication = communication;
        communication.setCallback(this);
    }

    public void start() {
        if(keepRunning)
            return;

        if(!communication.isConnected()) {
            try {
                communication.connect();
            } catch (Exception communicationConnectException) { //TODO: colocar stacktrace no log
                System.out.println("Connection failed. Cause:\n"
                        + communicationConnectException.getMessage());
                System.out.println("Server start aborted!");
                return; //abort the server start
            }
        }

        //before server start procedures
        try {
            subscribeDatabaseChannels();
        } catch (Exception databaseChannelsSubscriptionException) { //TODO: colocar stacktrace no log
            System.out.println("Channel subscription failed. Cause:\n"
                    + databaseChannelsSubscriptionException.getMessage());
            System.out.println("Server start aborted!");
            return; //abort the server start
        }

        //start the server
        keepRunning = true;
        new Thread(this).start();
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
        List<ProducersChannelsPathsEntity> producersChannelsPathsEntities =
            PersistenceUtilities.getEntityManagerInstance()
                .createQuery("SELECT pcpv FROM ProducersChannelsPathsEntity AS pcpv " +
                                "WHERE pcpv.isProducerPrefix = 1",
                        ProducersChannelsPathsEntity.class).getResultList();
        if(producersChannelsPathsEntities == null) {
            System.err.println("No producers channels to subscribe!");
            return;
        }

        for (ProducersChannelsPathsEntity producerChannelPathEntity : producersChannelsPathsEntities) {
            if(producerChannelPathEntity.getIsProducerPrefix() == 1) //if is producer prefix channel then subscribe the subscriber operations channels
                subscribeSubscribersOperationsChannels(producerChannelPathEntity.getPath()); //because all channels with this prefix are subscribed
        }
    }

    private void subscribeSubscribersOperationsChannels(String producerChannelPath) throws Exception{
        communication.subscribe(producerChannelPath
                + Constants.SUBSCRIBER_REGISTRATION_CHANNEL_SUFFIX); //Producer channel to subscriber registration
        communication.subscribe(producerChannelPath
                + Constants.SUBSCRIBER_UNREGISTRATION_CHANNEL_SUFFIX); //Producer channel to subscriber unregistration
        communication.subscribe(producerChannelPath
                + Constants.SUBSCRIBER_SUBSCRIPTION_CHANNEL_SUFFIX); //Producer channel to subscriber subscription
        communication.subscribe(producerChannelPath
                + Constants.SUBSCRIBER_UNSUBSCRIPTION_CHANNEL_SUFFIX); //Producer channel to subscriber unsubscription
        communication.subscribe(producerChannelPath
                + Constants.ALL_CHANNELS_SUFFIX); //All producer channels
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
