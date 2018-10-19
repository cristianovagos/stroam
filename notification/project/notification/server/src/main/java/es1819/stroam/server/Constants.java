package es1819.stroam.server;

public class Constants {

    //Channels constants
    public static final String SERVICE_CHANNEL_PREFIX = "/notTheService";
    public static final String COMMAND_RESULT_CHANNEL_SUFFIX = "/commandResult"; //Used to send operation results (Service and User should subscribe)
    public static final String REGISTER_CHANNEL_SUFFIX = "/register";
    public static final String UNREGISTER_CHANNEL_SUFFIX = "/unregister";
    public static final String UPDATE_CHANNEL_SUFFIX = "/update";
    public static final String PUSH_NOTIFICATION_ENABLED_SUFFIX = "/pushNotificationEnabled";
    public static final String EMAIL_NOTIFICATION_ENABLED_SUFFIX = "/emailNotificationEnabled";
    public static final String PHONE_NOTIFICATION_ENABLED_SUFFIX = "/phoneNotificationEnabled";
    public static final String PUSH_SUFFIX = "/push";
    public static final String EMAIL_SUFFIX = "/email";
    public static final String PHONE_SUFFIX = "/phone";
    public static final String PUSH_NOTIFICATION_SUFFIX = "/pushNotification"; //Used to send user push notifications (User should subscribe)

    public static final String ALL_USERS_CHANNELS_SUFFIX_ARRAY[] = {
            Constants.COMMAND_RESULT_CHANNEL_SUFFIX,
            Constants.EMAIL_NOTIFICATION_ENABLED_SUFFIX,
            Constants.EMAIL_SUFFIX,
            Constants.PHONE_NOTIFICATION_ENABLED_SUFFIX,
            Constants.PHONE_SUFFIX,
            Constants.PUSH_NOTIFICATION_ENABLED_SUFFIX,
            Constants.PUSH_SUFFIX,
            Constants.UNREGISTER_CHANNEL_SUFFIX,
            Constants.UPDATE_CHANNEL_SUFFIX };

}
