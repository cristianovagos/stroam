package es1819.stroam.catalog.model.notifications;

import lombok.Data;

@Data
public class Email {
    private String to;
    private String subject;
    private String body;
}
