package es1819.stroam.notification.server.core.handler;

import es1819.stroam.notification.server.core.message.Message;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class EmailHandler extends Handler {

    public EmailHandler() {
        super();
    }

    @Override
    public void run() {
        while (true) {
            try { threadRunController.acquire(); } catch (InterruptedException ignored) { continue; }

            if(!keepRunning)
                break;

            try { processingQueueAccessControllerMutex.acquire(); } catch (InterruptedException ignored) { continue; }
            Message message = processingQueue.poll();
            processingQueueAccessControllerMutex.release();

            if(message == null)
                continue;

            String emailAddress = message.getMailAddress();
            if(emailAddress == null || emailAddress.isEmpty()) {
                //TODO: enviar resposta com o request id a dizer que o endereço de email é nulo ou vazio e que o canal usado deve ser verificado
                System.out.println("received a phone message to process with a null or empty phone number"); //TODO: debug
                continue;
            }

            String emailMessageBody = message.getMailBody();
            if(emailMessageBody == null || emailMessageBody.isEmpty()) {
                //TODO: enviar resposta com o request id a dizer que o corpo da mensagem de email é nulo ou vazio e que nao sera enviado
                System.out.println("received a email message to process with a null or empty body"); //TODO: debug
                continue;
            }

            //TODO: enviar email
        }
    }
}
