package es1819.stroam.notification.server.core.handler;

import es1819.stroam.notification.server.Constants;
import es1819.stroam.notification.server.core.message.Message;
import es1819.stroam.notification.server.core.message.RequestMessage;
import es1819.stroam.notification.server.core.message.ResponseMessage;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Base64;
import java.util.Date;

public class EmailHandler extends Handler {

    private ResponseSenderHandler responseSenderHandler;

    public EmailHandler(ResponseSenderHandler responseSenderHandler) {
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

            String emailAddress = requestMessage.getMailAddress();
            if(emailAddress == null || emailAddress.isEmpty()) {
                responseSenderHandler.handle(
                        new ResponseMessage(HandleResultType.EMAIL_ADDRESS_NULL_OR_EMPTY, requestMessage.getRequestId())
                                .setReason("received a email requestMessage to process " +
                                        "with a null or empty email address"));
                continue;
            }

            String emailMessageBody = requestMessage.getMailBody();
            if(emailMessageBody == null || emailMessageBody.isEmpty()) {
                responseSenderHandler.handle(
                        new ResponseMessage(HandleResultType.EMAIL_BODY_NULL_OR_EMPTY, requestMessage.getRequestId())
                                .setReason("received a email requestMessage to process with a null or empty body"));
                continue;
            }

            String decodedEmailMessageBody;
            try {
                decodedEmailMessageBody = new String(Base64.getDecoder().decode(emailMessageBody));
            } catch (IllegalArgumentException messageDecodeException) {
                messageDecodeException.printStackTrace(); //TODO: logar excepçao

                responseSenderHandler.handle(
                        new ResponseMessage(HandleResultType.EMAIL_BODY_DECODE_ERROR, requestMessage.getRequestId())
                                .setReason("received a email requestMessage to process with an invalid encoded body"));
                continue;
            }

            if(decodedEmailMessageBody.isEmpty()) {
                responseSenderHandler.handle(
                        new ResponseMessage(HandleResultType.EMAIL_DECODED_BODY_NULL_OR_EMPTY,
                                requestMessage.getRequestId())
                                .setReason("received a phone requestMessage to process " +
                                        "with a null or empty decoded body"));
                continue;
            }

            String decodedEmailMessageSubject = "";
            String emailMessageSubject = requestMessage.getMailSubject();
            if(emailMessageSubject != null && !emailMessageSubject.isEmpty()) {
                try {
                    decodedEmailMessageSubject = new String(Base64.getDecoder().decode(emailMessageSubject));
                } catch (IllegalArgumentException messageDecodeException) {
                    messageDecodeException.printStackTrace(); //TODO: logar excepçao

                    responseSenderHandler.handle(
                            new ResponseMessage(HandleResultType.EMAIL_SUBJECT_DECODE_ERROR,
                                    requestMessage.getRequestId())
                                    .setReason("received a email requestMessage to process " +
                                            "with an invalid encoded subject"));
                }
            }

            //TODO: melhorar codigo e colocar a registar no log tambem
            if(Constants.runtimeProperties.getProperty(Constants.PROPERTY_DEBUG_MODE_NAME)
                    .equalsIgnoreCase("true")) {
                System.out.println("Sending email to: " + emailAddress);
                System.out.println("Subject: " + decodedEmailMessageSubject);
                System.out.println("Body: " + decodedEmailMessageBody);
            }

            try {
                sendEmail(emailAddress, decodedEmailMessageSubject, decodedEmailMessageBody);
            } catch (MessagingException sendEmailException) {
                sendEmailException.printStackTrace(); //TODO: logar excepçao

                responseSenderHandler.handle(
                        new ResponseMessage(HandleResultType.EMAIL_SENDING_ERROR, requestMessage.getRequestId())
                                .setReason("an unknown error occurred while sending email. Try again later"));
                continue;
            }

            //email send success
            responseSenderHandler.handle(new ResponseMessage(
                    HandleResultType.EMAIL_SENDING_SUCCESS, requestMessage.getRequestId())
                    .setReason("email successfully sended to " + emailAddress));
        }
    }

    private void sendEmail(String recipientMailAddress, String subject, String body) throws MessagingException {
        if(Constants.runtimeProperties == null)
            throw new IllegalArgumentException("email server settings not found. Please check the configuration file");

        String username = Constants.runtimeProperties.getProperty("mail.username");

        Session session;
        if(Constants.runtimeProperties.getProperty("mail.smtp.auth").equalsIgnoreCase("true"))
            session = Session.getInstance(Constants.runtimeProperties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(
                            username,
                            Constants.runtimeProperties.getProperty("mail.password"));
                }
            });
        else
            session = Session.getInstance(Constants.runtimeProperties);

        session.setDebug(Constants.runtimeProperties.getProperty(Constants.PROPERTY_DEBUG_MODE_NAME)
                .equalsIgnoreCase("true"));

        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(username));
        email.setRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(recipientMailAddress));
        email.setSubject(subject);
        email.setContent(body, Constants.runtimeProperties.getProperty("mail.content.type"));
        email.setSentDate(new Date());
        Transport.send(email);
    }
}