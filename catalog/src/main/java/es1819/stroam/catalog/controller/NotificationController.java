package es1819.stroam.catalog.controller;

import es1819.stroam.catalog.model.notifications.Email;
import es1819.stroam.catalog.model.notifications.Notification;
import es1819.stroam.catalog.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Slf4j
@RestController
@RequestMapping("/api/v1/notification")
public class NotificationController {

    @Autowired
    private NotificationService service;

    @RequestMapping(value = "/push", method = POST)
    public ResponseEntity<Object> sendPushNotification(@RequestBody Notification notification) {
        try {
            service.connect();
            service.sendPushNotification(notification.getTopic(), notification.getMessage());
            service.disconnect();
        } catch (Exception e) {
            log.error("Exception on connecting to Notification Server: " + e.getMessage());
            return ResponseEntity.badRequest().body("Failed to send push notification.");
        }

        log.info("Sent push notification to \"" + notification.getTopic() + "\": " + notification.getMessage().toString());
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/email", method = POST)
    public ResponseEntity<Object> sendEmailNotification(@RequestBody Email email) {
        try {
            service.connect();
            service.sendEmail(email);
            service.disconnect();
        } catch (Exception e) {
            log.error("Exception on connecting to Notification Server: " + e.getMessage());
            return ResponseEntity.badRequest().body("Failed to send push notification.");
        }

        log.info("Sent email to \"" + email.getTo() + "\", with subject \"" + email.getSubject() + "\", with body: " +
                email.getBody());
        return ResponseEntity.ok().build();
    }
}
