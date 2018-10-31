package es1819.stroam.notification.server;

public class Constants {

    //Channel constants
    public static final String CHANNEL_SEPARATOR = "/";
    public static final String CHANNEL_SERVICE_PREFIX = "notTheService";
    public static final String CHANNEL_ALL_PREFIX = "#";
    public static final String CHANNEL_EMAIL_PREFIX = "email";
    public static final String CHANNEL_PHONE_PREFIX = "phone";

    //Json constants
    public static final String JSON_REQUEST_ID_KEY = "requestId";
    public static final String JSON_EMAIL_SUBJECT_KEY = "subject";
    public static final String JSON_EMAIL_PHONE_BODY_KEY = "body";

    //Phone message constants
    public static final int PHONE_MESSAGE_MAX_CHARACTERS= 160;
}
