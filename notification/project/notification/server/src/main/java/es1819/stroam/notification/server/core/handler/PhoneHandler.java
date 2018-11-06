package es1819.stroam.notification.server.core.handler;

import es1819.stroam.notification.server.Constants;
import es1819.stroam.notification.server.core.message.Message;

import java.util.Base64;

public class PhoneHandler extends Handler {

    public PhoneHandler() {
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

            String phoneNumber = message.getPhoneNumber();
            if(phoneNumber == null || phoneNumber.isEmpty()) {
                //TODO: enviar resposta com o request id a dizer que o numero de telefone é nulo ou vazio e que o canal usado deve ser verificado
                System.out.println("received a phone message to process with a null or empty phone number"); //TODO: debug
                continue;
            }

            String phoneMessageBody = message.getPhoneBody();
            if(phoneMessageBody == null || phoneMessageBody.isEmpty()) {
                //TODO: enviar resposta com o request id a dizer que a mensagem de telefone é nula ou vazia e que nao sera enviada
                System.out.println("received a phone message to process with a null or empty body"); //TODO: debug
                continue;
            }


            String decodedPhoneMessageBody;
            try {
                decodedPhoneMessageBody = new String(Base64.getDecoder().decode(phoneMessageBody));
            } catch (IllegalArgumentException messageDecodeException) {
                //TODO: enviar resposta com o request id a dizer que ocorreu um erro ao fazer o decode da mensagem de telefone e que nao sera enviada
                System.out.println("received a phone message to process with an invalid encoded body"); //TODO: debug
                continue;
            }

            if(decodedPhoneMessageBody.isEmpty()) {
                //TODO: enviar resposta com o request id a dizer que a mensagem de telefone é nula ou vazia e que nao sera enviada
                System.out.println("received a phone message to process with a null or empty decoded body"); //TODO: debug
                continue;
            }

            if(decodedPhoneMessageBody.length() > Constants.PHONE_MESSAGE_MAX_CHARACTERS) {
                //TODO: enviar resposta com o request id a dizer que a mensagem de telefone é demasiado comprida e que nao sera enviada
                System.out.println("received a phone message to " +
                        "process with more than " + Constants.PHONE_MESSAGE_MAX_CHARACTERS + "characters long"); //TODO: debug
                continue;
            }

            //TODO: envia a mensagem
            System.out.println("Message sended for: " + message.getPhoneNumber());
            System.out.println("Message body: " + decodedPhoneMessageBody);
        }
    }
}
