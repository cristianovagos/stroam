package es1819.stroam.catalog.model.notifications;

import lombok.Data;

@Data
public class Notification {
    private String topic;
    private NotificationMessage message;
}
