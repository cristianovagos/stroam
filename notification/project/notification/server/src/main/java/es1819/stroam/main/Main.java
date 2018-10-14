package es1819.stroam.main;

import es1819.stroam.commons.communication.Communication;
import es1819.stroam.server.Server;

public class Main {

    public static void main(String[] args) {
        final Thread mainThread = Thread.currentThread();
        /*try {
            PrintStream o = new PrintStream(new FileOutputStream("./systemerr.txt", true));
            System.setErr(o);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/

        System.out.println("    _   _       _  _____ _          ____                  _          \n" +
                "   | \\ | | ___ | ||_   _| |__   ___/ ___|  ___ _ ____   _(_) ___ ___ \n" +
                "   |  \\| |/ _ \\| __|| | | '_ \\ / _ \\___ \\ / _ | '__\\ \\ / | |/ __/ _ \\\n" +
                "   | |\\  | (_) | |_ | | | | | |  __/___) |  __| |   \\ V /| | (_|  __/\n" +
                "   |_| \\_|\\___/ \\__||_| |_| |_|\\___|____/ \\___|_|    \\_/ |_|\\___\\___|\n" +
                "##################  MQTT based notification service  ###################\n");


        //TODO: Debug only
        Communication communication = new Communication("ws://localhost:1884");
        Server server = new Server(communication);
        server.start();

        //Shutdown hook used to stop all the running threads before program stops
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Server is stopping...");
            server.stop();

            try { mainThread.join(); } catch (InterruptedException ignored) {}
        }));
    }
}
