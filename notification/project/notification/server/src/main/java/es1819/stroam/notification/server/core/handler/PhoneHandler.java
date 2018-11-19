package es1819.stroam.notification.server.core.handler;

import es1819.stroam.notification.commons.Constants;
import es1819.stroam.notification.server.core.message.Message;
import es1819.stroam.notification.server.core.message.RequestMessage;
import es1819.stroam.notification.server.core.message.ResponseMessage;

import java.util.Base64;

public class PhoneHandler extends Handler {

    private ResponseSenderHandler responseSenderHandler;

    public PhoneHandler(ResponseSenderHandler responseSenderHandler) {
        super();

        if(responseSenderHandler == null)
            throw new IllegalArgumentException("responseSenderHandler cannot be null or empty");

        this.responseSenderHandler = responseSenderHandler;
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

            if(message instanceof ResponseMessage) {
                //TODO: colocar excepção abaixo no log
                new IllegalArgumentException("received a message to process of unexpected type of ResponseMessage").printStackTrace(); //TODO: debug
                continue;
            }
            RequestMessage requestMessage = (RequestMessage)message;

            String phoneNumber = requestMessage.getPhoneNumber();
            if(phoneNumber == null || phoneNumber.isEmpty()) {
                responseSenderHandler.handle(
                        new ResponseMessage(HandleResultType.PHONE_NUMBER_NULL_OR_EMPTY, requestMessage.getRequestId())
                                .setReason("received a phone requestMessage to process " +
                                        "with a null or empty phone number"));
                continue;
            }

            String phoneMessageBody = requestMessage.getPhoneBody();
            if(phoneMessageBody == null || phoneMessageBody.isEmpty()) {
                responseSenderHandler.handle(
                        new ResponseMessage(HandleResultType.PHONE_BODY_NULL_OR_EMPTY, requestMessage.getRequestId())
                                .setReason("received a phone requestMessage to process with a null or empty body"));
                continue;
            }


            String decodedPhoneMessageBody;
            try {
                decodedPhoneMessageBody = new String(Base64.getDecoder().decode(phoneMessageBody));
            } catch (IllegalArgumentException messageDecodeException) {
                messageDecodeException.printStackTrace(); //TODO: logar excepçao

                responseSenderHandler.handle(
                        new ResponseMessage(HandleResultType.PHONE_BODY_DECODE_ERROR, requestMessage.getRequestId())
                                .setReason("received a phone requestMessage to process with an invalid encoded body"));
                continue;
            }

            if(decodedPhoneMessageBody.isEmpty()) {
                responseSenderHandler.handle(
                        new ResponseMessage(HandleResultType.PHONE_DECODED_BODY_NULL_OR_EMPTY,
                                requestMessage.getRequestId())
                                .setReason("received a phone requestMessage to process " +
                                        "with a null or empty decoded body"));
                continue;
            }

            if(decodedPhoneMessageBody.length() > Constants.PHONE_MESSAGE_MAX_CHARACTERS) {
                responseSenderHandler.handle(
                        new ResponseMessage(HandleResultType.PHONE_BODY_TOO_LONG, requestMessage.getRequestId())
                                .setReason("received a phone requestMessage to " +
                                        "process with more than " + Constants.PHONE_MESSAGE_MAX_CHARACTERS +
                                        "characters long"));
                continue;
            }

            //TODO: envia a mensagem
            System.out.println("RequestMessage sended for: " + requestMessage.getPhoneNumber());
            System.out.println("RequestMessage body: " + decodedPhoneMessageBody);

            /*try {
                //TODO: enviar mensagem para arduino
            } catch (... sendPhoneMessageException) {
                sendPhoneMessageException.printStackTrace(); //TODO: logar excepçao

                responseSenderHandler.handle(
                        new ResponseMessage(HandleResultType.PHONE_SENDING_ERROR, requestMessage.getRequestId())
                                .setReason("an unknown error occurred while sending the phone message. Try again later"));
                continue;
            }*/

            //phone message send success
            responseSenderHandler.handle(new ResponseMessage(
                    HandleResultType.PHONE_SENDING_SUCCESS, requestMessage.getRequestId())
                    .setReason("phone message successfully sended to " + phoneNumber));
        }
    }
}
