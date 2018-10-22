package es1819.stroam.server.constants;

import es1819.stroam.server.logs.LogSeverity;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Strings {

    //General strings
    public static final String SEE_LOG_REGISTERY = "See the log registry for more detailed information";
    public static final String BROKER_CONNECTION_ESTABLISHMENT_STARTED = "Connecting to broker server...";
    public static final String BROKER_CONNECTION_ESTABLISHMENT_SUCCEEDED = "Connection to broker server succeeded";
    public static final String CHANNEL_SUBSCRIPTION_STARTED = "Creating all required communication channels...";
    public static final String CHANNEL_SUBSCRIPTION_SUCCEEDED = "All channels creation succeeded";

    //Log and Log Severities strings
    public static String LOG_FORMATTED_LINE(LogSeverity logSeverity,
                                                         Timestamp timestamp, String message, Throwable throwable) {
        StringBuilder result = new StringBuilder(String.format("%11s - %19s: %s",
                logSeverity, new SimpleDateFormat("dd/MM/YYYY hh:mm:ss").format(timestamp.getTime()), message));

        if(throwable != null)
            result.append("\n")
                    .append("Stack trace: ").append(throwable.toString());
        return result.toString();
    }

    public static String LOG_NO_WRITE_PERMISSIONS(String fileDirectoryPath) {
        return "No write permission in " + fileDirectoryPath;
    }

    public static final String LOG_INFORMATION_DESCRIPTION =
            "Information that may be helpful and do not represent a problem. For example, start or end of a " +
                    "service or process, settings used, etc.";
    public static final String LOG_DEBUG_DESCRIPTION =
            "Information that is diagnostically helpful for programmers or system administrators";
    public static final String LOG_WARNING_DESCRIPTION =
            "Something that could potentially cause a problem in the service but from which it is possible " +
                    "to recover automatically.";
    public static final String LOG_ERROR_DESCRIPTION =
            "Fatal error for correct system operation but not service. Typically this type of error involves " +
                    "an action of the administrator or user";
    public static final String LOG_FATAL_DESCRIPTION = "Any error that force a shutdown of the service to prevent " +
            "data loss (or further data loss).";
}
