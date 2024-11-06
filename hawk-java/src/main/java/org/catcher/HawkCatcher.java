package org.catcher;

import java.util.logging.Level;
import java.util.logging.Logger;

public class HawkCatcher {

    private static final Logger logger = Logger.getLogger(HawkCatcher.class.getName());
    private static int errorCount = 0;
    private static int warningCount = 0;

    // Initialize global error handler and logging
    static {
        Thread.setDefaultUncaughtExceptionHandler((thread, exception) -> {
            logError("Uncaught exception in thread " + thread.getName() + ": " + exception.getMessage());
            exception.printStackTrace();
        });
    }

    public static void logError(String message) {
        errorCount++;
        logger.log(Level.SEVERE, message);
        System.err.println("Error: " + message); // Print to console
    }

    public static void logWarning(String message) {
        warningCount++;
        logger.log(Level.WARNING, message);
        System.out.println("Warning: " + message); // Print to console
    }

    public static void reportStatus() {
        String statusMessage = "Current Status:\nErrors: " + errorCount + "\nWarnings: " + warningCount;
        System.out.println(statusMessage);
    }
}
