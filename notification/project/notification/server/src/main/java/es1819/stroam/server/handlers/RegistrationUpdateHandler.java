package es1819.stroam.server.handlers;

import es1819.stroam.server.constants.Constants;
import es1819.stroam.server.messages.Message;
import es1819.stroam.server.messages.MessagePayload;
import es1819.stroam.server.messages.MessageType;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class RegistrationUpdateHandler implements Runnable, Handler {

    private boolean keepRunning;
    private Queue<Message> registrationMessagesQueue = new LinkedList<>();
    private Semaphore registrationMessagesQueueAccessController = new Semaphore(1);
    private Semaphore threadRunController = new Semaphore(0);

    public RegistrationUpdateHandler() {
        //Empty constructor
    }

    public void start() {
        if(keepRunning)
            return;

        new Thread(this).start();
        keepRunning = true;
    }

    public void stop() {
        if(!keepRunning)
            return;

        keepRunning = false;
        threadRunController.release();
    }

    @Override
    public void run() {
        while (true) {
            try { threadRunController.acquire(); } catch (InterruptedException ignored) {}

            if(!keepRunning)
                break;

            Message receivedRegistrationMessage = registrationMessagesQueue.poll();
            if(receivedRegistrationMessage == null) {
                //TODO: logar warning que mensagem nula foi recebida ou ouve uma tentativa de retirar um elemento da fila quanto esta estava vazia
                continue;
            }

            //Starts the message json data validation
            MessagePayload messagePayload;
            try {
                messagePayload = new MessagePayload(receivedRegistrationMessage.getPayload());
            } catch (IllegalArgumentException messagePayloadParseException) {
                //TODO: registar como warning que houve um problema no parse da mensagem e passar excepção. Avisar que a mensagem foi descartada
                continue;
            }

            if(receivedRegistrationMessage.getType() == MessageType.SERVICE_REGISTRATION) {

            }

            //TODO: ############################################################################################
            //TODO: # arranjar forma de fazer parse dos valores e ir definindo esses valores nas ObjectEntity  #
            //TODO: # no final deve ser necessario apenas persistir o objecto. Ver casos de update             #
            //TODO: ############################################################################################
        }
    }

    @Override
    public void handleMessage(Message message) {
        try { registrationMessagesQueueAccessController.acquire(); } catch (InterruptedException ignored) {}
        registrationMessagesQueue.offer(message);
        registrationMessagesQueueAccessController.release();

        threadRunController.release();
    }
}
