package es1819.stroam.server.handlers;

import es1819.stroam.persistence.entities.UserEntity;
import es1819.stroam.persistence.utilities.PersistenceUtilities;
import es1819.stroam.server.ServerOperation;
import es1819.stroam.server.constants.Constants;
import es1819.stroam.server.messages.Message;
import es1819.stroam.server.messages.MessagePayload;
import es1819.stroam.server.utilities.GeneralUtilities;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import java.util.*;
import java.util.concurrent.Semaphore;

public class NotificationHandler implements Runnable, Handler {

    private boolean keepRunning;
    private long lastTimedEntityManagerUse;
    private ServerOperation serverOperation;
    private Queue<Message> messagesHandleQueue = new LinkedList<>();
    private Semaphore messagesHandleQueueAccessControllerMutex = new Semaphore(1);
    private Semaphore threadRunController = new Semaphore(0);
    private EntityManager entityManager;
    private Timer entityManagerCloseTimer;

    public NotificationHandler(ServerOperation serverOperation) {
        if(serverOperation == null)
            throw new IllegalArgumentException("serverOperation cannot be null or empty");

        this.serverOperation = serverOperation;
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

    @Override
    public void handleMessage(Message message) {
        try { messagesHandleQueueAccessControllerMutex.acquire(); } catch (InterruptedException ignored) {}
        messagesHandleQueue.offer(message);
        messagesHandleQueueAccessControllerMutex.release();

        threadRunController.release();
    }

    @Override
    public void run() {
        while (true) {
            try { threadRunController.acquire(); } catch (InterruptedException ignored) {}

            if(!keepRunning)
                break;

            Message messageHandle;
            try { messagesHandleQueueAccessControllerMutex.acquire(); } catch (InterruptedException ignored) {}
            messageHandle = messagesHandleQueue.poll();
            messagesHandleQueueAccessControllerMutex.release();

            if(messageHandle == null) {
                //TODO: registar no log que uma mensagem nula foi retirada da fila
                continue;
            }

            MessagePayload messageHandlePayload;
            try {
                messageHandlePayload = new MessagePayload(messageHandle.getPayload()); //Not null guaranteed
            } catch (IllegalArgumentException messagePayloadParseException) {
                //TODO: registar que foi recebida uma mensagem com um payload mal formado. enviar resposta de falha. Mensagem foi ignorada
                //TODO: se a flag debug estiver activada registar o payload recebido como string
                messagePayloadParseException.printStackTrace();
                continue;
            }

            UserEntity userEntity;
            entityManager = getTimedEntityManagerInstance();
            try {
                userEntity = entityManager.createQuery("SELECT u FROM UserEntity u " +
                        "WHERE u.active = 1 AND u.id LIKE: userId", UserEntity.class)
                        .setParameter("userId", messageHandle.getUserId().toString())
                        .getSingleResult();
            } catch (NonUniqueResultException noSingleResultQueryResultException) {
                //TODO: registar como ERRO que existem dois users com o mesmo id atribuido
                //TODO: responder como falha de notificação nao enviada
                continue;
            } catch (NoResultException ignored) {
                //TODO: logar como warning que o user com o userid nao existe ou nao esta activo
                continue;
            }

            switch (messageHandle.getType()) {
                case USER_PUSH:
                    userPushProcess(messageHandle.getServiceId(), userEntity, messageHandlePayload); break;
                case USER_EMAIL:
                    userEmailProcess(userEntity, messageHandlePayload); break;
                case USER_PHONE:
                    userPhoneProcess(userEntity, messageHandlePayload); break;
                default:
                    //TODO: logar o servidor colocou na fila de processamento uma mensagem nao destinada a este handler
            }
        }
    }

    private void userPushProcess(UUID serviceId, UserEntity userEntity, MessagePayload messagePayload) {
        if(userEntity.getPushNotification() == 0)
            return; //TODO: enviar resposta de falha

        String responseChannel = GeneralUtilities.createString(
                Constants.CHANNEL_SEPARATOR,
                Constants.CHANNEL_SERVICE_PREFIX,
                Constants.CHANNEL_SEPARATOR,
                serviceId.toString(),
                Constants.CHANNEL_SEPARATOR,
                userEntity.getId(),
                Constants.CHANNEL_SEPARATOR,
                Constants.CHANNEL_PUSH_NOTIFICATION_SUFFIX);

        try {
            serverOperation.send(responseChannel, messagePayload.getPushNotificationBody());
        } catch (Exception sendException) {
            //TODO: logar que ocorreu um erro ao enviar a notificacao para o utilizador
            sendException.printStackTrace();
        }

        System.out.println("Push sended to " + userEntity.getId() + " in channel: " + responseChannel); //TODO: debug
        System.out.println("Push body: " + new String(messagePayload.getPushNotificationBody())); //TODO: debug
    }

    private void userEmailProcess(UserEntity userEntity, MessagePayload messagePayload) {
        String emailAddress = userEntity.getEmailAddress();

        if(userEntity.getEmailNotification() == 0 || emailAddress == null || emailAddress.isEmpty())
            return; //TODO: enviar resposta de falha

        System.out.println("Email sended to " + userEntity.getId()); //TODO: debug
        System.out.println("Email subject: " + messagePayload.getEmailNotificationSubject()); //TODO: debug
        System.out.println("Email body: " + messagePayload.getEmailNotificationBody()); //TODO: debug
        //TODO: enviar email para o user
    }

    private void userPhoneProcess(UserEntity userEntity, MessagePayload messagePayload) {
        String phoneNumber = userEntity.getPhoneNumber();

        if(userEntity.getPhoneNotification() == 0 || phoneNumber == null || !phoneNumber.isEmpty())
            return; //TODO: enviar resposta de falha

        System.out.println("Phone SMS sended to " + userEntity.getId()); //TODO: debug
        System.out.println("Phone SMS body: " + messagePayload.getPhoneNotificationBody()); //TODO: debug
        //TODO: enviar sms para o user
    }

    private EntityManager getTimedEntityManagerInstance() { //TODO: optimizar se possivel
        if (entityManager == null)
            entityManager = PersistenceUtilities.getEntityManagerInstance();

        if(entityManagerCloseTimer == null) {
            entityManagerCloseTimer = new Timer();
            entityManagerCloseTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if(System.currentTimeMillis() - lastTimedEntityManagerUse >=
                            Constants.TIMED_CLOSE_ENTITY_MANAGER_TIMEOUT) {
                        try { messagesHandleQueueAccessControllerMutex.acquire(); }
                        catch (InterruptedException ignored) {}
                        int messagesHandleQueueCount = messagesHandleQueue.size();
                        messagesHandleQueueAccessControllerMutex.release();

                        if(messagesHandleQueueCount == 0 && entityManager.isOpen() &&
                                !entityManager.getTransaction().isActive()){
                            entityManager.close();
                            entityManager = null;

                            this.cancel();
                            entityManagerCloseTimer = null;
                            System.out.println("########## TIMED ENTITY MANAGER CLOSED ##########"); //TODO: debug. apagar
                        }
                    }
                }
            }, Constants.TIMED_CLOSE_ENTITY_MANAGER_TIMEOUT, 1);
        }

        lastTimedEntityManagerUse = System.currentTimeMillis();
        return entityManager;
    }
}
