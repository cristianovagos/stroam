package es1819.stroam.main;

import es1819.stroam.commons.communication.Communication;
import es1819.stroam.persistence.entities.ChannelEntity;
import es1819.stroam.server.Server;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class Main {

    public static void main(String[] args) {
        System.out.println("    _   _       _  _____ _          ____                  _          \n" +
                "   | \\ | | ___ | ||_   _| |__   ___/ ___|  ___ _ ____   _(_) ___ ___ \n" +
                "   |  \\| |/ _ \\| __|| | | '_ \\ / _ \\___ \\ / _ | '__\\ \\ / | |/ __/ _ \\\n" +
                "   | |\\  | (_) | |_ | | | | | |  __/___) |  __| |   \\ V /| | (_|  __/\n" +
                "   |_| \\_|\\___/ \\__||_| |_| |_|\\___|____/ \\___|_|    \\_/ |_|\\___\\___|\n" +
                "##################  MQTT based notification service  ###################\n");


        //TODO: Debug only
        Communication communication = new Communication("ws://localhost:1884");
        try {
            communication.connect();
            communication.subscribe("/teste");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Server server = new Server(communication);
        server.start();

        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("NotTheServicePersistenceUnit");

        ChannelEntity c = new ChannelEntity();
        c.setPath("/stroam/teste/users");

        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(c);
        entityManager.getTransaction().commit();
        entityManager.close();

    }
}
