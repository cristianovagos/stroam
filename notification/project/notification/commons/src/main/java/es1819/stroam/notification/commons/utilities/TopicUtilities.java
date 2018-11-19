package es1819.stroam.notification.commons.utilities;

import es1819.stroam.notification.commons.Constants;

public class TopicUtilities {

    public static String getEmailTopic(String emailAddress) {
        if(emailAddress == null || emailAddress.isEmpty())
            throw new IllegalArgumentException("emailAddress cannot be null or empty");
        return createTopic(Constants.CHANNEL_SERVICE_PREFIX, Constants.CHANNEL_EMAIL_PREFIX, emailAddress);
    }

    public static String getPhoneTopic(String phoneNumber) {
        if(phoneNumber == null || phoneNumber.isEmpty())
            throw new IllegalArgumentException("phoneNumber cannot be null or empty");
        return createTopic(Constants.CHANNEL_SERVICE_PREFIX, Constants.CHANNEL_PHONE_PREFIX, phoneNumber);
    }

    public static String createTopic(String... channelPartsWithoutSeparators) {
        if(channelPartsWithoutSeparators == null || channelPartsWithoutSeparators.length == 0)
            throw new IllegalArgumentException("channelPartsWithoutSeparators cannot be null or empty");

        StringBuilder channelBuilder = new StringBuilder();
        for (String channelPart : channelPartsWithoutSeparators) {
            channelBuilder.append(Constants.CHANNEL_SEPARATOR)
                    .append(channelPart);
        }
        return channelBuilder.toString();
    }
}
