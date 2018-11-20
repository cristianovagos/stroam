package es1819.stroam.notification.server.core.handler;

import es1819.stroam.notification.commons.Constants;
import es1819.stroam.notification.server.ServerSender;
import es1819.stroam.notification.commons.communication.message.Message;
import es1819.stroam.notification.commons.communication.message.request.RequestMessage;
import es1819.stroam.notification.commons.communication.message.response.ResponseMessage;

public class ResponseSenderHandler extends Handler {

    private ServerSender serverSender;

    public ResponseSenderHandler(ServerSender serverSender) {
        super();

        if(serverSender == null)
            throw new IllegalArgumentException("responseSenderHandler cannot be null or empty");

        this.serverSender = serverSender;
    }

    @Override
    public void run() {
        while(true) {
            try { threadRunController.acquire(); } catch (InterruptedException ignored) { continue; }

            if(!keepRunning)
                break;

            try { processingQueueAccessControllerMutex.acquire(); } catch (InterruptedException ignored) { continue; }
            Message message = processingQueue.poll();
            processingQueueAccessControllerMutex.release();

            if(message == null)
                continue;

            if(message instanceof RequestMessage) {
                //TODO: colocar excepção abaixo no log
                new IllegalArgumentException("received a message to process of unexpected type of RequestMessage").printStackTrace(); //TODO: debug
                continue;
            }
            ResponseMessage responseMessage = (ResponseMessage)message;

            responseMessage.setTopic(Constants.CHANNEL_SEPARATOR +
                    Constants.CHANNEL_SERVICE_PREFIX +
                    Constants.CHANNEL_SEPARATOR +
                    Constants.CHANNEL_REQUEST_RESPONSE_PREFIX);
            try {
                serverSender.send(responseMessage);
            } catch (Exception messageSendException) {
                //TODO: registar no log que ocorreu um erro ao enviar uma mensagem
                messageSendException.printStackTrace(); //TODO: debug
            }
        }
    }
}
