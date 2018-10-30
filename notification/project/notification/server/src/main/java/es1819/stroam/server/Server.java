package es1819.stroam.server;

import es1819.stroam.commons.communication.Communication;
import es1819.stroam.commons.communication.CommunicationCallback;
import es1819.stroam.persistence.utilities.PersistenceUtilities;
import es1819.stroam.server.constants.Constants;
import es1819.stroam.server.constants.Strings;
import es1819.stroam.server.handlers.NotificationHandler;
import es1819.stroam.server.handlers.RegistrationUpdateHandler;
import es1819.stroam.server.messages.Message;
import es1819.stroam.server.utilities.GeneralUtilities;

import javax.persistence.EntityManager;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class Server implements Runnable, CommunicationCallback, ServerOperation {

    private boolean keepRunning;
    private Communication communication;
    private RegistrationUpdateHandler registrationUpdateHandler;
    private NotificationHandler notificationHandler;
    private Thread serverThread;
    private Queue<Message> receivedMessagesQueue = new LinkedList<>();
    private Semaphore receivedMessagesQueueAccessControllerMutex = new Semaphore(1);
    private Semaphore threadRunController = new Semaphore(0);

    public Server(Communication communication) { //TODO: ver se mantem Handler messageHandler) {
        if(communication == null)
            throw new IllegalArgumentException("Communication cannot be null");

        this.communication = communication;
        communication.setCallback(this);
    }

    public boolean start() { //TODO: resolver problema se dado start e stop de imeditato
        if(keepRunning)
            return false;

        System.out.println(Strings.BROKER_CONNECTION_ESTABLISHMENT_STARTED);
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
        } System.out.println(Strings.BROKER_CONNECTION_ESTABLISHMENT_SUCCEEDED);

        //Before server start procedures
        System.out.println(Strings.CHANNEL_SUBSCRIPTION_STARTED);
        try {
            subscribeDatabaseChannels();
        } catch (Exception subscribeDatabaseChannelsException) {
            subscribeDatabaseChannelsException.printStackTrace();//TODO: colocar stacktrace no log
            System.out.println("Server start failed. Cause: "
                    + subscribeDatabaseChannelsException.getMessage() + "\n"
                    + Strings.SEE_LOG_REGISTERY);
            System.out.println("Server start aborted!");
            return false; //abort the server start
        } System.out.println(Strings.CHANNEL_SUBSCRIPTION_SUCCEEDED);

        //Start the server threads and resources
        registrationUpdateHandler = new RegistrationUpdateHandler(this);
        registrationUpdateHandler.start();

        notificationHandler = new NotificationHandler(this);
        notificationHandler.start();

        //Start the server
        keepRunning = true;
        (serverThread = new Thread(this)).start();
        return true;
    }

    public boolean stop() { //TODO: resolver problema se dado start e stop de imeditato
        if(!keepRunning)    //TODO: testar bem e verificar se todas as threads sao correctamente terminadas mesmo em caso de interrupção
            return false;

        //Stop all server threads and resources
        registrationUpdateHandler.stop();
        notificationHandler.stop();

        /*try { serverThread.join(); } catch (InterruptedException ignored) {}
        finally {
            communication.disconnect();
            keepRunning = false;
        }*/

        communication.disconnect();

        keepRunning = false;
        threadRunController.release(); //release the thread to finish it
        return true;
    }

    @Override
    public void run() {
        while (true) {
            try { threadRunController.acquire(); } catch (InterruptedException ignored) {}

            if(!keepRunning)
                break;

            try { receivedMessagesQueueAccessControllerMutex.acquire(); } catch (InterruptedException ignored) {}
            Message receivedMessage = receivedMessagesQueue.poll();
            receivedMessagesQueueAccessControllerMutex.release();

            if(receivedMessage == null) {
                //TODO: logar warning que mensagem nula foi recebida ou ouve uma tentativa de retirar um elemento da fila quanto esta estava vazia
                continue;
            }

            switch (receivedMessage.getType()) {
                case SERVICE_REGISTRATION:
                case SERVICE_UNREGISTRATION:
                case SERVICE_USER_REGISTRATION:
                case SERVICE_USER_UNREGISTRATION:
                case SERVICE_USER_UPDATE:
                    registrationUpdateHandler.handleMessage(receivedMessage);
                    break;
                case USER_PUSH:
                case USER_EMAIL:
                case USER_PHONE:
                    notificationHandler.handleMessage(receivedMessage);
                    break;
            }
        }
    }

    @Override
    public void messageArrived(String channel, byte[] payload) { //This method should only enqueue received messages
        try {
            receivedMessagesQueueAccessControllerMutex.acquire();
            receivedMessagesQueue.offer(new Message(channel, payload));
        } catch (IllegalArgumentException messageParseException) { //Generated by Message.parse
            //TODO: Logar como WARNING que uma mensagem invalida for ignorada e registar stacktrace de messageParseException
            receivedMessagesQueueAccessControllerMutex.release();
        } catch (InterruptedException ignored) {} //Generated by receivedMessagesQueueAccessControllerMutex

        receivedMessagesQueueAccessControllerMutex.release();
        threadRunController.release();
    }

    @Override
    public void messageDeliveryComplete(byte[] messageBytes) {

    }

    @Override
    public void connectionLost(Throwable throwable) {
        //TODO: Lgar excepção e tratar perda de ligação
    }

    @Override
    public void subscribeChannels(String serviceId) throws Exception {
        if(serviceId == null || serviceId.isEmpty())
            throw new IllegalArgumentException("Service id cannot be null or empty");

        String serviceChannelBase = GeneralUtilities.createString(Constants.CHANNEL_SEPARATOR,
                Constants.CHANNEL_SERVICE_PREFIX, Constants.CHANNEL_SEPARATOR, serviceId,
                Constants.CHANNEL_SEPARATOR);

        subscribeChannelAndLog(serviceChannelBase + Constants.CHANNEL_REGISTER_SUFFIX); // .../register
        subscribeChannelAndLog(serviceChannelBase + Constants.CHANNEL_UNREGISTER_SUFFIX);// .../unregister
    }

    @Override
    public void subscribeChannels(String serviceId, String userId) throws Exception {
        if(serviceId == null || serviceId.isEmpty())
            throw new IllegalArgumentException("Service id cannot be null or empty");
        if(userId == null || userId.isEmpty())
            throw new IllegalArgumentException("User id cannot be null or empty");

        String userChannelBase = GeneralUtilities.createString(Constants.CHANNEL_SEPARATOR,
                Constants.CHANNEL_SERVICE_PREFIX, Constants.CHANNEL_SEPARATOR,
                serviceId, Constants.CHANNEL_SEPARATOR, userId,
                Constants.CHANNEL_SEPARATOR);

        for (int i = 0; i < Constants.CHANNEL_ALL_USERS_SUFFIX_ARRAY.length; i++) {
            subscribeChannelAndLog(userChannelBase
                    + Constants.CHANNEL_ALL_USERS_SUFFIX_ARRAY[i]); // .../<all user suffixes>
        }
    }

    @Override
    public void unsubscribeChannels(String serviceId) throws Exception {
        if(serviceId == null || serviceId.isEmpty())
            throw new IllegalArgumentException("Service id cannot be null or empty");

        String serviceChannelBase = GeneralUtilities.createString(Constants.CHANNEL_SEPARATOR,
                Constants.CHANNEL_SERVICE_PREFIX, Constants.CHANNEL_SEPARATOR, serviceId,
                Constants.CHANNEL_SEPARATOR);

        unsubscribeChannelAndLog(serviceChannelBase + Constants.CHANNEL_REGISTER_SUFFIX); // .../register
        unsubscribeChannelAndLog(serviceChannelBase + Constants.CHANNEL_UNREGISTER_SUFFIX);// .../unregister

        //TODO: tambem deve dessubscrever todos os canais associados aos utilizadores deste serviço. Verficar se se pode usar o metodo de subscrever para fazer a operação inversa tambem
    }

    @Override
    public void unsubscribeChannels(String serviceId, String userId) throws Exception {
        if(serviceId == null || serviceId.isEmpty())
            throw new IllegalArgumentException("Service id cannot be null or empty");
        if(userId == null || userId.isEmpty())
            throw new IllegalArgumentException("User id cannot be null or empty");

        String userChannelBase = GeneralUtilities.createString(Constants.CHANNEL_SEPARATOR,
                Constants.CHANNEL_SERVICE_PREFIX, Constants.CHANNEL_SEPARATOR,
                serviceId, Constants.CHANNEL_SEPARATOR, userId,
                Constants.CHANNEL_SEPARATOR);

        for (int i = 0; i < Constants.CHANNEL_ALL_USERS_SUFFIX_ARRAY.length; i++) {
            unsubscribeChannelAndLog(userChannelBase
                    + Constants.CHANNEL_ALL_USERS_SUFFIX_ARRAY[i]); // .../<all user suffixes>
        }
    }

    @Override
    public void send(String channel, byte[] payload) throws Exception {
        if(channel == null || channel.isEmpty())
            return;
        if(payload == null || payload.length == 0)
            return;

        communication.send(channel, payload);
    }

    private void subscribeDatabaseChannels() throws Exception { //TODO: optimizar
        //Subscribe the channel to register new services /notTheService/register
        subscribeChannelAndLog(GeneralUtilities.createString(
                Constants.CHANNEL_SEPARATOR, Constants.CHANNEL_SERVICE_PREFIX,
                Constants.CHANNEL_SEPARATOR, Constants.CHANNEL_REGISTER_SUFFIX));

        EntityManager entityManager = PersistenceUtilities.getEntityManagerInstance();
        if(entityManager == null) {
            throw new NullPointerException("entityManager reference is null");
        }

        //Subscribe the services channels /notTheService/<serviceId>/...
        List<String> serviceIds = entityManager
                .createQuery("SELECT s.id FROM ServiceEntity AS s WHERE s.active = 1", String.class)
                .getResultList();
        if(serviceIds == null || serviceIds.isEmpty()) {
            System.err.println("No registered services");
            return;
        }

        try {
            for (String serviceId : serviceIds) {
                subscribeChannels(serviceId);
            }
        } catch (IllegalArgumentException emptyNullServiceId) {
            //TODO: logar que houve uma tentativa de subscriçao de um canal com um id de serviço vazio ou nulo
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

        try {
            for (Object[] serviceIdUserIdEntry : serviceIdUserIdEntries) {
                subscribeChannels(serviceIdUserIdEntry[0].toString(), serviceIdUserIdEntry[1].toString());
            }
        } catch (IllegalArgumentException emptyNullServiceId) {
            //TODO: logar que houve uma tentativa de subscriçao de um canal com um id de serviço ou utilizador vazio ou nulo
        }
    }

    private void subscribeChannelAndLog(String channel) throws Exception {
        communication.subscribe(channel); //TODO: colocar canal subscrito log
        System.out.println("Available: " + channel); //TODO: debug
    }

    private void unsubscribeChannelAndLog(String channel) throws Exception {
        communication.unsubscribe(channel); //TODO: colocar canal subscrito log
        System.out.println("Unavailable: " + channel); //TODO: debug
    }
}
