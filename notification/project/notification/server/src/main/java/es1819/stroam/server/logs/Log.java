package es1819.stroam.server.logs;

import es1819.stroam.persistence.entities.LogEntity;
import es1819.stroam.persistence.utilities.PersistenceUtilities;
import es1819.stroam.server.constants.Constants;
import es1819.stroam.server.constants.Strings;
import es1819.stroam.server.utilities.GeneralUtilities;

import javax.persistence.EntityManager;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Timestamp;

public class Log {

    private static Log instance;
    private static boolean started;
    private PrintStream printStream;

    private Log(PrintStream printStream) {
        this.printStream = printStream; //No null reference guaranteed by method startLog
    }

    public static Log getInstance() {
        return instance;
    }

    public static void startLog(String logFilePath) {
        if(started)
            return;

        if(logFilePath == null || logFilePath.isEmpty())
            logFilePath = Constants.LOG_DEFAULT_DIRECTORY;


        File logFile = new File(logFilePath);
        boolean noWritePermissionsFlag = !logFile.canWrite();

        if(logFile.isDirectory()) {
            logFile = new File(GeneralUtilities.createString(
                    logFilePath, Constants.FILE_DIRECTORY_SEPARATOR, Constants.LOG_CURRENT_FILE_NAME));
            noWritePermissionsFlag = !logFile.canWrite(); //TODO: COMO O FICHEIRO NAO EXISTE DIZ QUE NAO PODE SER ESCRITO

            if(!noWritePermissionsFlag && logFile.length() >= Constants.LOG_FILE_MAXIMUM_SIZE_BYTES) {
                try {
                    logFile.renameTo(new File(Constants.LOG_COMPRESS_FILE_NAME));
                    if(GeneralUtilities.compressFile(logFile, new File(GeneralUtilities.createString(
                            logFilePath, Constants.FILE_DIRECTORY_SEPARATOR,
                            Constants.LOG_COMPRESSED_FILE_RESULT_NAME)))) { //in case of compression succeeds

                        if(logFile.delete())
                            logFile = new File(GeneralUtilities.createString(
                                    logFilePath, Constants.FILE_DIRECTORY_SEPARATOR, Constants.LOG_CURRENT_FILE_NAME));
                    } else
                        logFile.renameTo(new File(Constants.LOG_CURRENT_FILE_NAME)); //rename to original file name
                } catch (Exception currentLogFileCompressionException) {
                    System.out.println(
                            Strings.LOG_FORMATTED_LINE(LogSeverity.WARNING, new Timestamp(System.currentTimeMillis()),
                            "Cannot compress the log file: " + logFile.getPath(),
                                    currentLogFileCompressionException));
                }
            }
        }

        try {
            if (!logFile.exists() && !logFile.createNewFile()) {
                System.out.println(
                        Strings.LOG_FORMATTED_LINE(LogSeverity.ERROR, new Timestamp(System.currentTimeMillis()),
                        "Cannot create the log file: " + logFile.getPath() + ". " +
                                "In case of database failure, logs will be printed to default output", null));
            } else instance = new Log(new PrintStream(logFile)); //Creates the new log instance
        } catch (FileNotFoundException logFileNotFoundException) {
            System.out.println(
                    Strings.LOG_FORMATTED_LINE(LogSeverity.WARNING, new Timestamp(System.currentTimeMillis()),
                            "Failed to associate log file with logger. Verify that the file exists: " +
                                    logFile.getPath() + ". In case of database failure, logs will be printed to " +
                                    "default output", null));
        } catch (IOException logFileCreationException) {
            Strings.LOG_FORMATTED_LINE(LogSeverity.WARNING, new Timestamp(System.currentTimeMillis()),
                    "Cannot create the log file: " + logFile.getPath(),
                    logFileCreationException);
        } finally {
            if(instance == null)
                instance = new Log(System.err);

            started = true;
        }

        if(noWritePermissionsFlag)
            System.out.println("No write permissions for file " + logFile.getPath() + ". " +
                "In case of database failure, logs will be printed to default output");
    }

    public void close() {
        if(!started || printStream == null)
            return;

        printStream.close();
        started = false;
    }

    public void log(LogSeverity logSeverity, String message) {
        if (message == null || message.isEmpty())
            return;

        log(logSeverity, message, null); //basically the same operations but with stack trace so...
    }

    public void log(LogSeverity logSeverity, String message, Throwable throwable) { //Responsible for log persistence
        if ((message == null || message.isEmpty()) && throwable == null)
            return;
        Timestamp logTimeStamp = new Timestamp(System.currentTimeMillis());

        LogEntity newLogEntity = new LogEntity();
        if (throwable != null)
            newLogEntity.setStackTrace(throwable.toString());

        newLogEntity.setMessage(message);
        newLogEntity.setSeverityId(logSeverity.getValue());
        newLogEntity.setTimestamp(logTimeStamp);

        EntityManager entityManager = PersistenceUtilities.getEntityManagerInstance();
        if (entityManager == null) {
            String logLine = Strings.LOG_FORMATTED_LINE(
                    logSeverity, logTimeStamp, message, throwable);

            if(started && printStream != null)
                printStream.println(logLine);
            else
                System.out.println(logLine);
            return;
        }

        entityManager.getTransaction().begin();
        entityManager.persist(newLogEntity);
        entityManager.getTransaction().commit();
        entityManager.close();
    }
}