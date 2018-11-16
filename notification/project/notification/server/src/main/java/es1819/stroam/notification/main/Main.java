package es1819.stroam.notification.main;

import es1819.stroam.notification.commons.communication.Communication;
import es1819.stroam.notification.server.Constants;
import es1819.stroam.notification.server.Server;
import es1819.stroam.notification.server.utilities.GeneralUtilities;

import java.io.*;

public class Main {

    static {
        /*try {
            PrintStream errFilePrintStream = new PrintStream(
                    new FileOutputStream("./systemErr.txt", true));
            System.setErr(errFilePrintStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/ //TODO: nao usar no desenvolvimento
    }

    public static void main(String[] args) {

        System.out.println("    _   _       _  _____ _          ____                  _          \n" +
                "   | \\ | | ___ | ||_   _| |__   ___/ ___|  ___ _ ____   _(_) ___ ___ \n" +
                "   |  \\| |/ _ \\| __|| | | '_ \\ / _ \\___ \\ / _ | '__\\ \\ / | |/ __/ _ \\\n" +
                "   | |\\  | (_) | |_ | | | | | |  __/___) |  __| |   \\ V /| | (_|  __/\n" +
                "   |_| \\_|\\___/ \\__||_| |_| |_|\\___|____/ \\___|_|    \\_/ |_|\\___\\___|\n" +
                "##################  The simplest way of stay updated  ##################\n");

        System.out.println("Starting the server... ");
        try { //TODO: optimizar este codigo. informar se forem carregadas as propriedades padão
            Constants.loadConfigurationFile(new File(Constants.currentWorkingDirectoryPath));
        } catch (IOException configurationFileReadException) {
            configurationFileReadException.printStackTrace(); //TODO: debug
        }

        String brokerAddress = Constants.runtimeProperties.getProperty(Constants.PROPERTY_BROKER_ADDRESS_NAME);
        if(brokerAddress == null || brokerAddress.isEmpty()) {
            System.out.println(Constants.PROPERTY_BROKER_ADDRESS_NAME + " not found in the configuration file \"" +
                    Constants.CONFIGURATION_FILE_NAME + "\" or the property does not contain any endpoint specified ");
            return;
        }

        Communication communication = new Communication(brokerAddress);
        Server server = new Server(communication);

        //Shutdown hook used to stop all the running threads before program stops
        final Thread mainThread = Thread.currentThread();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Server is stopping...");

            try {
                server.stop();
                mainThread.join();
            } catch (Exception serverStopException) {
                if(!(serverStopException instanceof InterruptedException))
                    serverStopException.printStackTrace(); //TODO: debug
            }
        }));

        try {
            if (server.start())
                System.out.println("Server successfully started");
        } catch (Exception serverStartException) {
            serverStartException.printStackTrace(); //TODO: debug
        }
    }

}
