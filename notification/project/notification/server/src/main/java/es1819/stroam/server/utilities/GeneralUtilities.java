package es1819.stroam.server.utilities;

public class GeneralUtilities {

    public static String createString(String... channelParts) {
        if(channelParts == null || channelParts.length == 0)
            return null;

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < channelParts.length; i++) {
            //TODO: fazer algum tipo de validação se necessário
            result.append(channelParts[i]);
        }
        return result.toString();
    }

}
