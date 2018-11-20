package es1819.stroam.catalog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import es1819.stroam.catalog.model.notifications.Notification;
import es1819.stroam.notification.client.NotTheServiceClient;
import es1819.stroam.notification.client.NotTheServiceClientCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Slf4j
@RestController
@RequestMapping("/api/v1/notification")
public class NotificationController implements NotTheServiceClientCallback {
    @Value("${stroam.notificationserver.host}")
    private String NOTIFICATION_SERVER_HOST;

    @Value("${stroam.notificationserver.port}")
    private String NOTIFICATION_SERVER_PORT;

    private NotTheServiceClient client;

    @Autowired
    private ObjectMapper mapper;

    @PostConstruct
    public void initNotificationClient() {
        client = new NotTheServiceClient("ws://" + NOTIFICATION_SERVER_HOST + ":" +  NOTIFICATION_SERVER_PORT);
        client.setCallback(this);
    }

    @RequestMapping(value = "/push", method = POST)
    public ResponseEntity<Object> sendPushNotification(@RequestBody Notification notification) {
        try {
            client.connect();
            client.sendPush("/" + notification.getTopic(), mapper.writeValueAsString(notification.getMessage()));
        } catch (Exception e) {
            log.error("Exception on connecting to Notification Server: " + e.getMessage());
            return ResponseEntity.badRequest().body("Failed to send push notification.");
        }

        log.info("Sent push notification to \"" + notification.getTopic() + "\": " + notification.getMessage().toString());
        return ResponseEntity.ok().build();
    }

    @Override
    public void onConnect() {
        log.info("Connected to the notification service");
    }

    @Override
    public void onResponseRequestArrived(String s, int i, String s1) {
        log.info("Connected to the notification service - s: " + s + ", i: " + i + ", s1: " + s1);
    }

    // s -> TOPIC
    // s1 -> PAYLOAD
    @Override
    public void onPushArrived(String s, String s1) {
        log.info("pushArrived: s: " + s + ", s1: " + s1);
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
