package es1819.stroam.catalog.model.notifications;

import lombok.Data;

@Data
public class NotificationMessage {
    private String title;
    private String message;
    private String url_path;
    private String image;
}
