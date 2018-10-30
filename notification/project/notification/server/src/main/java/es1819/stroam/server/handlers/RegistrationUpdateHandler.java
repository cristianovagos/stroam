package es1819.stroam.server.handlers;

import es1819.stroam.persistence.entities.ServiceEntity;
import es1819.stroam.persistence.entities.ServiceUserEntity;
import es1819.stroam.persistence.entities.UserEntity;
import es1819.stroam.persistence.utilities.PersistenceUtilities;
import es1819.stroam.server.ServerOperation;
import es1819.stroam.server.constants.Constants;
import es1819.stroam.server.messages.Message;
import es1819.stroam.server.messages.MessagePayload;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import java.util.*;
import java.util.concurrent.Semaphore;

public class RegistrationUpdateHandler implements Runnable, Handler {

    private boolean keepRunning;
    private long lastTimedEntityManagerUse;
    private ServerOperation serverOperation;
    private Queue<Message> messagesHandleQueue = new LinkedList<>();
    private Semaphore messagesHandleQueueAccessControllerMutex = new Semaphore(1);
    private Semaphore threadRunController = new Semaphore(0);
    private EntityManager entityManager;
    private Timer entityManagerCloseTimer;

    public RegistrationUpdateHandler(ServerOperation serverOperation) {
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

            switch (messageHandle.getType()) {
                case SERVICE_REGISTRATION:
                    serviceRegistrationMessageProcess(messageHandlePayload); break;
                case SERVICE_UNREGISTRATION:
                    serviceUnregistrationMessageProcess(messageHandle.getServiceId(), messageHandlePayload); break;
                case SERVICE_USER_REGISTRATION:
                case SERVICE_USER_UPDATE:
                    userRegistrationUpdateMessageProcess(messageHandle.getServiceId(), messageHandle.getUserId(), messageHandlePayload); break;
                case SERVICE_USER_UNREGISTRATION:
                    userUnregistrationMessageProcess(messageHandle.getServiceId(), messageHandle.getUserId(), messageHandlePayload); break;
                    default:
                        //TODO: logar o servidor colocou na fila de processamento uma mensagem nao destinada a este handler
            }
        }
    }

    //TODO: deve ser devolvido o service id de alguma forma para usar na api cliente
    private void serviceRegistrationMessageProcess(MessagePayload messagePayload) {
        ServiceEntity serviceEntity = new ServiceEntity();

        String serviceName = messagePayload.getName();
        if(serviceName != null && !serviceName.isEmpty())
            serviceEntity.setName(serviceName);

        String serviceId = UUID.randomUUID().toString();
        serviceEntity.setId(serviceId);
        serviceEntity.setActive((byte)1);
        persist(serviceEntity);

        try {
            serverOperation.subscribeChannels(serviceId);
        } catch (Exception serviceChannelSubscriptionException) {
            //TODO: logar excepcao que ocorreu um erro ao subscrever os canais do servico
            serviceChannelSubscriptionException.printStackTrace();
        }
        //TODO: enviar resposta de sucesso
    }

    private void serviceUnregistrationMessageProcess(UUID serviceId, MessagePayload messagePayload) {
        entityManager = getTimedEntityManagerInstance();

        ServiceEntity serviceEntity;
        try {
            serviceEntity = entityManager.createQuery("SELECT s FROM ServiceEntity s " +
                    "WHERE s.active = 1 AND s.id LIKE: serviceId", ServiceEntity.class)
                    .setParameter("serviceId", serviceId.toString())
                    .getSingleResult();
        } catch (NonUniqueResultException noSingleResultQueryResultException) {
            //TODO: registar como ERRO que existem dois services com o mesmo id atribuido
            //TODO: responder como falha de serviço nao registado
            return;
        } catch (NoResultException ignored) {
            //TODO: logar como warning que o serviço com o serviceId nao existe
            return;
        }

        UUID payloadServiceId = messagePayload.getId();
        if(payloadServiceId != null && payloadServiceId.equals(serviceId)) {
            serviceEntity.setActive((byte)0);
            persist(serviceEntity);

            try {
                serverOperation.unsubscribeChannels(payloadServiceId.toString());
            } catch (Exception serviceChannelUnsubscriptionException) {
                //TODO: logar excepcao que ocorreu um erro ao dessubscrever os canais do servico
                serviceChannelUnsubscriptionException.printStackTrace();
            }
        } else {
            //TODO: enviar mensagem de resposta a dizer que o id é obrigatorio
            System.out.println("Id is mandatory");
            return;
        }
    }

    private void userRegistrationUpdateMessageProcess(UUID serviceId, UUID userId, MessagePayload messagePayload) {
        boolean isUserUpdate = userId != null; //User update if it's true
        UserEntity userEntity;

        if(isUserUpdate) {
            entityManager = getTimedEntityManagerInstance();

            try {
                userEntity = entityManager.createQuery("SELECT u FROM UserEntity u " +
                        "WHERE u.active = 1 AND u.id LIKE: userId", UserEntity.class)
                        .setParameter("userId", userId.toString())
                        .getSingleResult();
            } catch (NonUniqueResultException noSingleResultQueryResultException) {
                //TODO: registar como ERRO que existem dois users com o mesmo id atribuido
                //TODO: responder como falha de utilizador nao registado
                return;
            } catch (NoResultException ignored) {
                //TODO: logar como warning que o user com o userid nao existe
                return;
            }
        } else
            userEntity = new UserEntity();

        String externalId = messagePayload.getExternalId();
        if(!isUserUpdate && externalId == null || externalId.isEmpty()) {
            //TODO: enviar mensagem de resposta a dizer que o id externo é obrigatorio
            System.out.println("External id is mandatory");
            return;
        } else if(!externalId.equals(userEntity.getExternalId()))
            userEntity.setExternalId(externalId);

        String name = messagePayload.getName();
        if(name != null && !name.isEmpty())
            userEntity.setName(name);

        String emailAddress = messagePayload.getEmailAddress();
        if(emailAddress != null && !emailAddress.isEmpty())
            userEntity.setEmailAddress(emailAddress);

        String phoneAddress = messagePayload.getPhoneNumber();
        if(phoneAddress != null && !phoneAddress.isEmpty())
            userEntity.setPhoneNumber(phoneAddress);

        Boolean pushNotification = messagePayload.isPushNotificationEnabled();
        if(pushNotification != null) {
            userEntity.setPushNotification((byte)(pushNotification ? 1 : 0));
        }

        Boolean emailNotification = messagePayload.isEmailNotificationEnabled();
        if(emailNotification != null)
            userEntity.setEmailNotification((byte)(emailNotification ? 1 : 0));

        Boolean phoneNotification = messagePayload.isPhoneNotificationEnabled();
        if(phoneNotification != null)
            userEntity.setPhoneNotification((byte)(phoneNotification ? 1 : 0));

        if(!isUserUpdate) {
            userId = UUID.randomUUID();
            userEntity.setId(userId.toString());
            userEntity.setActive((byte)1);

            ServiceUserEntity serviceUserEntity = new ServiceUserEntity();
            serviceUserEntity.setServiceId(serviceId.toString());
            serviceUserEntity.setUserId(userEntity.getId());

            persist(userEntity);
            persist(serviceUserEntity);

            try {
                serverOperation.subscribeChannels(serviceId.toString(), userId.toString());
            } catch (Exception userChannelSubscriptionException) {
                //TODO: logar excepcao que ocorreu um erro ao subscrever os canais do utilizador
                userChannelSubscriptionException.printStackTrace();
            }
        } else
            persist(userEntity);

        //TODO: enviar resposta a dizer que foi registado/actualizado com sucesso
    }

    private void userUnregistrationMessageProcess(UUID serviceId, UUID userId, MessagePayload messagePayload) {
        entityManager = getTimedEntityManagerInstance();

        UserEntity userEntity;
        try {
            userEntity = entityManager.createQuery("SELECT u FROM UserEntity u " +
                    "WHERE u.active = 1 AND u.id LIKE: userId", UserEntity.class)
                    .setParameter("userId", userId.toString())
                    .getSingleResult();
        } catch (NonUniqueResultException noSingleResultQueryResultException) {
            //TODO: registar como ERRO que existem dois users com o mesmo id atribuido
            //TODO: responder como falha de utilizador nao registado
            return;
        } catch (NoResultException ignored) {
            //TODO: logar como warning que o user com o userid nao existe
            return;
        }

        String externalId = messagePayload.getExternalId();
        if(externalId != null && !externalId.isEmpty()) {
            userEntity.setActive((byte)0);
            persist(userEntity);

            try {
                serverOperation.unsubscribeChannels(serviceId.toString(), userId.toString());
            } catch (Exception userChannelSubscriptionException) {
                //TODO: logar excepcao que ocorreu um erro ao dessubscrever os canais do utilizador
                userChannelSubscriptionException.printStackTrace();
            }
        } else {
            //TODO: enviar mensagem de resposta a dizer que o id externo é obrigatorio
            System.out.println("External id is mandatory");
            return;
        }
    }

    private void persist(Object object) {
        entityManager = getTimedEntityManagerInstance();

        entityManager.getTransaction().begin();
        entityManager.persist(object);
        entityManager.getTransaction().commit();
    }

    //The method bellow associates a timer with the EntityManager
    //If an instance is not used until the end of the scheduled time it will be closed
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
