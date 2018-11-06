package es1819.stroam.notification.server;

import es1819.stroam.notification.commons.communication.Communication;
import es1819.stroam.notification.commons.communication.CommunicationCallback;
import es1819.stroam.notification.server.core.handler.EmailHandler;
import es1819.stroam.notification.server.core.handler.PhoneHandler;
import es1819.stroam.notification.server.core.message.Message;
import es1819.stroam.notification.server.utilities.ChannelUtilities;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class Server implements Runnable, CommunicationCallback {

    private boolean keepRunning;
    private Communication communication;
    private EmailHandler emailHandler;
    private PhoneHandler phoneHandler;
    private Queue<Message> messageQueue = new LinkedList<>();
    private Semaphore messageQueueAccessControllerMutex = new Semaphore(1);
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
                ChannelUtilities.createChannel(
                        Constants.CHANNEL_SERVICE_PREFIX,
                        Constants.CHANNEL_ALL_PREFIX));

        emailHandler = new EmailHandler();
        emailHandler.start();

        phoneHandler = new PhoneHandler();
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

        communication.disconnect();
    }

    @Override
    public void run() {
        while (true) {
            try { threadRunController.acquire(); } catch (InterruptedException ignored) { continue; }

            if(!keepRunning)
                break;

            try { messageQueueAccessControllerMutex.acquire(); } catch (InterruptedException ignored) { continue; }
            //Mutual exclusion access
            Message message = messageQueue.poll();
            messageQueueAccessControllerMutex.release();

            switch (message.getType()) {
                case MAIL:
                    emailHandler.handle(message); break;
                case PHONE:
                    phoneHandler.handle(message); break;
                case RESPONSE:
            }
        }
    }

    @Override
    public void messageArrived(String channel, byte[] payload) {
        try { messageQueueAccessControllerMutex.acquire(); } catch (InterruptedException ignored) { return; }
        //Mutual exclusion access

        Message message;
        try {
            message = new Message(channel, payload);
            messageQueue.offer(message);
        } catch (IllegalArgumentException messageParseException) {
            messageParseException.printStackTrace(); //TODO: debug
            //TODO: registar no log que ocorreu um erro ao fazer parse de uma mensagem recebida
            //TODO: deve ser enviada resposta (ler linhas abaixo)
            /*Se calhar criar um novo tipo de mensagens (resposta ou erro) e neste caso colocar-la na fila na mesma
            a diferenca é que ela deve ser logo enviada nao chegando a ir para as threads de processamento
             */
        } finally {
            messageQueueAccessControllerMutex.release();
        }
        threadRunController.release(); //TODO: ### se o parse da mensagem der erro a thread é solta na mesma
    }

    @Override
    public void messageDeliveryComplete(byte[] bytes) {

    }

    @Override
    public void connectionLost(Throwable throwable) {

    }
}
