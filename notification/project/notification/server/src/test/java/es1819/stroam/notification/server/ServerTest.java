package es1819.stroam.notification.server;

import es1819.stroam.notification.commons.communication.Communication;
import org.json.JSONObject;
import org.junit.Test;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Base64;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;

import static junit.framework.TestCase.fail;

public class ServerTest {

    @Test
    public void generalTest() {
        Communication communication = new Communication("ws://localhost:1884");
        Server server = new Server(communication);

        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONObject mail = new JSONObject()
                .put(Constants.JSON_REQUEST_ID_KEY, UUID.randomUUID())
                .put(Constants.JSON_EMAIL_SUBJECT_KEY, Base64.getEncoder().encodeToString("subject test".getBytes()))
                .put(Constants.JSON_EMAIL_PHONE_BODY_KEY, Base64.getEncoder().encodeToString("body test".getBytes()));

        JSONObject phone = new JSONObject()
                .put(Constants.JSON_REQUEST_ID_KEY, UUID.randomUUID())
                .put(Constants.JSON_EMAIL_PHONE_BODY_KEY, Base64.getEncoder().encodeToString("body test".getBytes()));

        try {
            communication.send("/notTheService/email/test@stroam.com", mail.toString().getBytes());
            communication.send("/notTheService/phone/00351918403521", phone.toString().getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void mailSendTest() {
        //https://stackoverflow.com/questions/18778240/solve-error-javax-mail-authenticationfailedexception


        /*25 receiving email from other mailservers
        465 SSL Client email submission
        587 TLS Client email submission
        143 StartTLS IMAP client
        993 TLS/SSL IMAP client
        110 POP3 client
        995 TLS/SSL POP3 client*/


        boolean auth = true;
        boolean debug = true;

        Properties props = new Properties();
        props.put("mail.smtp.host","localhost");
        props.put("mail.smtp.port",587);
        props.put("mail.smtp.auth",true);
        props.put("mail.smtp.starttls.enable",true);
        //props.put("mail.smtp.ssl.enable",true);
        props.put("mail.smtp.ssl.trust","localhost");
        //props.put("mail.smtp.user","noreply@stroam.com");
        //props.put("mail.smtp.password","12345");

        Session session = null;
        if (auth) {
        props.put("mail.smtp.auth", true);
            session = Session.getInstance(props, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("noreply@stroam.com", "12345");
                }
            });
        }
        session.setDebug(debug);

        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress("noreplay@stroam.com"));
            InternetAddress[] address = {new InternetAddress("test@stroam.com")};
            message.setRecipients(Message.RecipientType.TO, address);
            message.setSubject("email de teste");
            message.setSentDate(new Date());
            message.setContent("<html><body><h1>HTML<br><img src=\"data:image/png;base64,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\"/></h1></body></html>", "text/html; charset=utf-8");
            Transport.send(message);
        } catch (MessagingException ex) {
            ex.printStackTrace();
            fail("Mail send fail");
        }


    }
}