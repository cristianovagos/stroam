package es1819.stroam.main;

import es1819.stroam.commons.communication.Communication;
import es1819.stroam.server.Server;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

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
        Communication communication = new Communication("ws://localhost:1884");
        Server server = new Server(communication);

        //Shutdown hook used to stop all the running threads before program stops
        final Thread mainThread = Thread.currentThread();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Server is stopping...");
            server.stop();

            try { mainThread.join(); } catch (InterruptedException ignored) {}
        }));

        if(server.start())
            System.out.println("Server successfully started");
    }
}
