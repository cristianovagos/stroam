package es1819.stroam.server.constants;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Constants {

    //General constants
    public static final String FILE_DIRECTORY_SEPARATOR = "/";

    //Log constants
    public static final int LOG_FILE_MAXIMUM_SIZE_BYTES = 10000000; //10 Megabytes
    public static final String LOG_DEFAULT_DIRECTORY = "./";
    public static final String LOG_CURRENT_FILE_NAME = "Log_Current.txt";
    public static final String LOG_COMPRESS_FILE_NAME = "Log_" +
            new SimpleDateFormat("ddMMyyyyHHmmss").format(new Date()) + ".txt";
    public static final String LOG_COMPRESSED_FILE_RESULT_NAME = "Log_" +
            new SimpleDateFormat("ddMMyyyyHHmmss").format(new Date()) + ".zip";

    //Channels constants
    public static final String CHANNEL_SEPARATOR = "/";
    public static final String CHANNEL_SERVICE_PREFIX = "notTheService";
    public static final String CHANNEL_COMMAND_RESULT_SUFFIX = "commandResult"; //Used to send operation results (Service and User should subscribe)
    public static final String CHANNEL_REGISTER_SUFFIX = "register";
    public static final String CHANNEL_UNREGISTER_SUFFIX = "unregister";
    public static final String CHANNEL_UPDATE_SUFFIX = "update";
    public static final String CHANNEL_PUSH_SUFFIX = "push";
    public static final String CHANNEL_EMAIL_SUFFIX = "email";
    public static final String CHANNEL_PHONE_SUFFIX = "phone";
    public static final String CHANNEL_PUSH_NOTIFICATION_SUFFIX = "pushNotification"; //Used to send user push notifications (User should subscribe)

    public static final String CHANNEL_ALL_USERS_SUFFIX_ARRAY[] = {
            Constants.CHANNEL_EMAIL_SUFFIX,
            Constants.CHANNEL_PHONE_SUFFIX,
            Constants.CHANNEL_PUSH_SUFFIX,
            Constants.CHANNEL_UNREGISTER_SUFFIX,
            Constants.CHANNEL_UPDATE_SUFFIX };

    //Message constants
    public static final String MESSAGE_ID_JSON_FIELD = "id";
    public static final String MESSAGE_EXTERNAL_ID_JSON_FIELD = "externalId";
    public static final String MESSAGE_NAME_JSON_FIELD = "name";
    public static final String MESSAGE_EMAIL_JSON_FIELD = "emailAddress";
    public static final String MESSAGE_PHONE_JSON_FIELD = "phoneNumber";
    public static final String MESSAGE_PUSH_NOTIFICATION_JSON_FIELD = "pushNotification";
    public static final String MESSAGE_EMAIL_NOTIFICATION_JSON_FIELD = "emailNotification";
    public static final String MESSAGE_PHONE_NOTIFICATION_JSON_FIELD = "phoneNotification";

    //Managed message queue constants
    public static final int MANAGE_QUEUE_DEFAULT_SIZE_LIMIT = 50;
}
