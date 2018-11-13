package es1819.stroam.notification.server.core.handler;

import es1819.stroam.notification.server.Constants;
import es1819.stroam.notification.server.core.message.Message;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Base64;
import java.util.Date;
import java.util.Properties;

public class EmailHandler extends Handler {

    private Properties outgoingEmailServerProperties;

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

            String decodedEmailMessageBody;
            try {
                decodedEmailMessageBody = new String(Base64.getDecoder().decode(emailMessageBody));
            } catch (IllegalArgumentException messageDecodeException) {
                //TODO: enviar resposta com o request id a dizer que ocorreu um erro ao fazer o decode da mensagem de email e que nao sera enviada
                System.out.println("received a email message to process with an invalid encoded body"); //TODO: debug
                continue;
            }

            String decodedEmailMessageSubject = "";
            String emailMessageSubject = message.getMailSubject();
            if(emailMessageSubject != null && !emailMessageSubject.isEmpty()) {
                try {
                    decodedEmailMessageSubject = new String(Base64.getDecoder().decode(emailMessageSubject));
                } catch (IllegalArgumentException messageDecodeException) {
                    //TODO: enviar resposta com o request id a dizer que ocorreu um erro ao fazer o decode do assunto da mensagem de email e que nao sera enviada
                    System.out.println("received a email message to process with an invalid encoded subject"); //TODO: debug
                }
            }

            //TODO: melhorar codigo e colocar a registar no log tambem
            if(Constants.DEBUG_MODE) {
                System.out.println("Sending email to: " + emailAddress);
                System.out.println("Subject: " + decodedEmailMessageSubject);
                System.out.println("Body: " + decodedEmailMessageBody);
            }

            try {
                sendEmail(emailAddress, decodedEmailMessageSubject, decodedEmailMessageBody);
            } catch (MessagingException sendEmailException) {
                //TODO: enviar resposta com o request id a dizer que ocorreu um erro ao enviar o email para x
                sendEmailException.printStackTrace();
            }
        }
    }

    private void sendEmail(String recipientMailAddress, String subject, String body) throws MessagingException {
        //TODO: colocar estas definições a serem lidas de um ficheiro de configuração e retirar daqui (colocar em Constants)
        //TODO: ver se as keys das properties se podem colocar na classe constants
        if(outgoingEmailServerProperties == null) {
            outgoingEmailServerProperties = new Properties();
            outgoingEmailServerProperties.put("mail.smtp.host", "localhost");
            outgoingEmailServerProperties.put("mail.smtp.port", 587);
            outgoingEmailServerProperties.put("mail.smtp.auth", true);
            outgoingEmailServerProperties.put("mail.smtp.starttls.enable", true);
            outgoingEmailServerProperties.put("mail.smtp.ssl.trust", "localhost");
            outgoingEmailServerProperties.put("mail.username", "noreply@stroam.com");
            outgoingEmailServerProperties.put("mail.password", "12345");
            outgoingEmailServerProperties.put("mail.content.type", "text/html; charset=utf-8");
        }

        String username = outgoingEmailServerProperties.getProperty("mail.username");

        Session session;
        if((boolean)outgoingEmailServerProperties.get("mail.smtp.auth"))
            session = Session.getInstance(outgoingEmailServerProperties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(
                            username,
                            outgoingEmailServerProperties.getProperty("mail.password"));
                }
            });
        else
            session = Session.getInstance(outgoingEmailServerProperties);

        session.setDebug(Constants.DEBUG_MODE);

        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(username));
        email.setRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(recipientMailAddress));
        email.setSubject(subject);
        email.setContent(body, outgoingEmailServerProperties.getProperty("mail.content.type"));
        email.setSentDate(new Date());
        Transport.send(email);
    }
}
