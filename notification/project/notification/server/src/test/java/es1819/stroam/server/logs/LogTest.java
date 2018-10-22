package es1819.stroam.server.logs;

import es1819.stroam.server.constants.Constants;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class LogTest {

    @Test
    public void startLog() {
        String logFileDirectory = "./"; //Test for directory path

        try {
            Log.startLog(logFileDirectory);
        } catch (Exception e) {
            fail(e.getStackTrace().toString());
        }

        File logFile = new File(logFileDirectory + Constants.FILE_DIRECTORY_SEPARATOR + Constants.LOG_CURRENT_FILE_NAME);
        if(!logFile.exists())
            fail("Log file not created");
        else {
            Log.getInstance().close();
            logFile.delete();
        }

        /*String logFileFullPath = "./Log.txt";
        try {
            Log.startLog(logFileFullPath);
        } catch (Exception e) {
            fail(e.getStackTrace().toString());
        }

        logFile = new File(logFileFullPath);
        if(!logFile.exists())
            fail("Log file not created");
        else {
            Log.getInstance().close();
            logFile.delete();
        }*/
    }

    @Test
    public void close() {
    }

    @Test
    public void log() {
    }

    @Test
    public void log1() {
    }
}