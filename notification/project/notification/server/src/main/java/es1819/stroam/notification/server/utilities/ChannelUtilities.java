package es1819.stroam.notification.server.utilities;

import es1819.stroam.notification.server.Constants;

public class ChannelUtilities {

    public static String createChannel(String... channelPartsWithoutSeparators) {
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
