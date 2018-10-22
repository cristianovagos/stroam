package es1819.stroam.server.logs;

import es1819.stroam.persistence.entities.LogSeverityEntity;
import es1819.stroam.persistence.utilities.PersistenceUtilities;
import es1819.stroam.server.constants.Strings;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

public enum LogSeverity {
    INFORMATION(getDatabaseId("INFORMATION")),
    DEBUG(getDatabaseId("DEBUG")),
    WARNING(getDatabaseId("WARNING")),
    ERROR(getDatabaseId("ERROR")),
    FATAL(getDatabaseId("FATAL"));

    private final int value;

    LogSeverity(int value) {
        this.value = value;
    }

    private static int getDatabaseId(String levelName) {
        EntityManager entityManager = PersistenceUtilities.getEntityManagerInstance();
        if(entityManager == null)
            throw new NullPointerException("entityManager reference is null"); //TODO: ver como logar erro

        LogSeverityEntity logSeverityEntity;
        try {
            logSeverityEntity = entityManager
                    .createQuery("SELECT ls FROM LogSeverityEntity ls WHERE ls.name LIKE :ls_n", LogSeverityEntity.class)
                    .setParameter("ls_n", levelName)
                    .getSingleResult();
        } catch (NoResultException ignored) {
            logSeverityEntity = new LogSeverityEntity();
            logSeverityEntity.setName(levelName);

            if ("INFORMATION".equals(levelName))
                logSeverityEntity.setDescription(Strings.LOG_INFORMATION_DESCRIPTION);
            else if ("DEBUG".equals(levelName))
                logSeverityEntity.setDescription(Strings.LOG_DEBUG_DESCRIPTION);
            else if ("WARNING".equals(levelName))
                logSeverityEntity.setDescription(Strings.LOG_WARNING_DESCRIPTION);
            else if ("ERROR".equals(levelName))
                logSeverityEntity.setDescription(Strings.LOG_ERROR_DESCRIPTION);
            else if ("FATAL".equals(levelName))
                logSeverityEntity.setDescription(Strings.LOG_FATAL_DESCRIPTION);

            entityManager.getTransaction().begin();
            entityManager.persist(logSeverityEntity);
            entityManager.getTransaction().commit();
            entityManager.close();
        }
        return logSeverityEntity.getId();
    }

    int getValue() {
        return value;
    }
}
