package es1819.stroam.persistence.utilities;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

public class PersistenceUtilities {

    public static EntityManager getEntityManagerInstance() {
        return Persistence
                .createEntityManagerFactory("NotTheServicePersistenceUnit")
                .createEntityManager();
    }

}
