package es1819.stroam.server;

import es1819.stroam.commons.communication.Communication;
import es1819.stroam.commons.communication.CommunicationCallback;
import es1819.stroam.server.handlers.Handler;

import java.util.LinkedList;
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

        keepRunning = true;
        new Thread(this).start();
    }

    public void stop() {
        if(!keepRunning)
            return;

        keepRunning = false;
        threadRunController.release();
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

    public void messageArrived(String topic, byte[] messageBytes) {
        try { receivedMessagesQueueAccessControllerMutex.acquire(); } catch (InterruptedException ignored) {}
        receivedMessagesQueue.offer(new MessageTopicBytes(topic, messageBytes));
        receivedMessagesQueueAccessControllerMutex.release();

        threadRunController.release();
    }

    public void messageDeliveryComplete(byte[] messageBytes) {

    }

    public void connectionLost(Throwable throwable) {

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
