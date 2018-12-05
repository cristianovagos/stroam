package es1819.stroam.catalog.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import es1819.stroam.catalog.model.notifications.Email;
import es1819.stroam.catalog.model.retrofit.authemail.AuthEmailInfo;
import es1819.stroam.catalog.model.retrofit.authemail.AuthEmailService;
import es1819.stroam.catalog.model.retrofit.frontend.FrontendChannelInfo;
import es1819.stroam.catalog.model.retrofit.frontend.FrontendService;
import es1819.stroam.notification.client.NotTheServiceClient;
import es1819.stroam.notification.client.NotTheServiceClientCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import javax.annotation.PostConstruct;

@Slf4j
@Service
public class NotificationService implements NotTheServiceClientCallback {

    @Value("${stroam.notificationserver.host}")
    private String NOTIFICATION_SERVER_HOST;

    @Value("${stroam.notificationserver.port}")
    private String NOTIFICATION_SERVER_PORT;

    @Autowired
    private FrontendService frontendService;

    @Autowired
    private AuthEmailService authEmailService;

    @Autowired
    private ObjectMapper mapper;

    private NotTheServiceClient client;

    @PostConstruct
    public void initNotificationClient() {
        client = new NotTheServiceClient("ws://" + NOTIFICATION_SERVER_HOST + ":" +  NOTIFICATION_SERVER_PORT);
        client.setCallback(this);
    }

    public void connect() throws Exception {
        client.connect();
    }

    public void disconnect() throws Exception {
        client.disconnect();
    }

    public boolean isConnected() {
        return client.isConnected();
    }

    public void subscribe(String topic) throws Exception {
        client.subscribe("/" + topic);
    }

    public void unsubscribe(String topic) throws Exception {
        client.unsubscribe("/" + topic);
    }

    public void sendPushNotification(String topic, Object message) throws Exception {
        client.sendPush("/" + topic, mapper.writeValueAsString(message));
    }

    public void sendEmail(Email email) throws Exception {
        client.sendEmail(email.getTo(), email.getSubject(), email.getBody());
    }

    public void sendEmail(String channelName, Email email) {
        try {
            if(!client.isConnected()) client.connect();
            Response<FrontendChannelInfo> response = frontendService.getUserSubscriptions(channelName).execute();
            if (response.isSuccessful()) {
                FrontendChannelInfo info = response.body();
                for (int userID : info.getUsers()) {
                    Response<AuthEmailInfo> emailResponse = authEmailService.getEmailByUserID(userID).execute();
                    if(emailResponse.isSuccessful()) {
                        AuthEmailInfo emailInfo = emailResponse.body();
                        client.sendEmail(emailInfo.getEmail(), email.getSubject(), email.getBody());
                    }
                    else {
                        log.error("Auth response of email not successful: " + emailResponse.errorBody().toString());
                    }
                }
            } else {
                log.error("Response not successful: " + response.errorBody().string());
            }
        } catch (Exception e) {
            log.error("Exception on sendEmail " + e.getMessage());
        }
    }

    @Override
    public void onConnect() {
        log.info("Connected to the notification service");
    }

    @Override
    public void onResponseRequestArrived(String s, int i, String s1) {
        log.info("Connected to the notification service - s: " + s + ", i: " + i + ", s1: " + s1);
    }

    @Override
    public void onPushArrived(String topic, String payload) {
        log.info("pushArrived: topic: " + topic + ", payload: " + payload);
    }

    @Override
    public void onConnectionLost(Throwable throwable) {
        log.info("connectionLost");
    }

    @Override
    public void onDisconnect() {
        log.info("onDisconnect");
    }

}
