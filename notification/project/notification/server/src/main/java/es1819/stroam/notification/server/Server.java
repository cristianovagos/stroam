package es1819.stroam.notification.server;

import es1819.stroam.notification.commons.Constants;
import es1819.stroam.notification.commons.communication.mqtt.Communication;
import es1819.stroam.notification.commons.communication.mqtt.CommunicationCallback;
import es1819.stroam.notification.server.core.handler.EmailHandler;
import es1819.stroam.notification.server.core.handler.HandleResultType;
import es1819.stroam.notification.server.core.handler.PhoneHandler;
import es1819.stroam.notification.server.core.handler.ResponseSenderHandler;
import es1819.stroam.notification.commons.communication.message.MessageUtilities;
import es1819.stroam.notification.commons.communication.message.request.RequestMessage;
import es1819.stroam.notification.commons.communication.message.response.ResponseMessage;
import es1819.stroam.notification.commons.utilities.TopicUtilities;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class Server implements Runnable, CommunicationCallback, ServerSender {

    private boolean keepRunning;
    private Communication communication;
    private ResponseSenderHandler responseSenderHandler;
    private EmailHandler emailHandler;
    private PhoneHandler phoneHandler;
    private Queue<RequestMessage> receivedRequestMessageQueue = new LinkedList<>();
    private Semaphore receivedMessageQueueAccessControllerMutex = new Semaphore(1);
    private static Semaphore threadRunController = new Semaphore(0);

    public Server(Communication communication) {
        if(communication == null)
            throw new IllegalArgumentException("Communication cannot be null");

        this.communication = communication;
        communication.setCallback(this);
    }

    public boolean start() throws Exception {
        if(keepRunning)
            return false;

        //Do all the procedures before server starts
        if(!communication.isConnected())
            communication.connect();

        communication.subscribe(
                TopicUtilities.createTopic(
                        Constants.CHANNEL_SERVICE_PREFIX,
                        Constants.CHANNEL_ALL_PREFIX));

        responseSenderHandler = new ResponseSenderHandler(this);
        responseSenderHandler.start();

        emailHandler = new EmailHandler(responseSenderHandler);
        emailHandler.start();

        phoneHandler = new PhoneHandler(responseSenderHandler);
        phoneHandler.start();

        //Starts the server
        keepRunning = true;
        new Thread(this).start();
        return true;
    }

    public void stop() throws Exception {
        if(!keepRunning)
            return;

        keepRunning = false;
        threadRunController.release();

        //Do all the procedures after server stops
        emailHandler.stop();
        phoneHandler.stop();
        responseSenderHandler.stop();

        communication.disconnect();
    }

    @Override
    public void run() {
        while (true) {
            try { threadRunController.acquire(); } catch (InterruptedException ignored) { continue; }

            if(!keepRunning)
                break;

            try { receivedMessageQueueAccessControllerMutex.acquire(); } catch (InterruptedException ignored) { continue; }
            //Mutual exclusion access
            RequestMessage requestMessage = receivedRequestMessageQueue.poll();
            receivedMessageQueueAccessControllerMutex.release();

            switch (requestMessage.getType()) {
                case MAIL:
                    emailHandler.handle(requestMessage); break;
                case PHONE:
                    phoneHandler.handle(requestMessage); break;
                case RESPONSE:
            }
        }
    }

    @Override
    public void send(ResponseMessage message) throws Exception {
        if(message == null)
            throw new IllegalArgumentException("received a null message to send");

        String channel = message.getTopic();
        if(channel == null || channel.isEmpty())
            throw new IllegalArgumentException("received a message to send with a null or empty destination topic");

        byte[] payload = message.getPayload();
        if(payload == null || payload.length == 0)
            throw new IllegalArgumentException("received a message to send with a null or empty payload");

        communication.send(channel, payload);
    }

    @Override
    public void messageArrived(String channel, byte[] payload) {
        if(!channel.contains(Constants.CHANNEL_EMAIL_PREFIX) && !channel.contains(Constants.CHANNEL_PHONE_PREFIX))
            return; //push notification message or other type of non request message

        //Mutual exclusion access
        try { receivedMessageQueueAccessControllerMutex.acquire(); } catch (InterruptedException ignored) { return; }

        RequestMessage requestMessage;
        try {
            requestMessage = new RequestMessage(channel, payload);
            receivedRequestMessageQueue.offer(requestMessage);
        } catch (IllegalArgumentException messageParseException) {
            messageParseException.printStackTrace(); //TODO: logar excepçao

            String requestId = MessageUtilities.recoverMalformedMessageRequestId(payload); //even if the message is malformed try recover request id
            responseSenderHandler.handle(
                    new ResponseMessage(
                            HandleResultType.UNKNOWN_ERROR.getResultCode(), requestId)
                            .setReason(messageParseException.getMessage()));
            return;
        } finally {
            receivedMessageQueueAccessControllerMutex.release(); //always executed even with the return in catch block
        }
        threadRunController.release(); //means that the requestMessage as been successfully parsed
    }

    @Override
    public void messageDeliveryComplete(byte[] bytes) {

    }

    @Override
    public void connectionLost(Throwable throwable) {

    }
}
