package so.java.hawk.catcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * HawkCatcher Class
 *
 * Provides methods for global error handling, logging errors and warnings, and reporting status.
 */
@Component
public class HawkCatcher {

    private static final Logger logger = LoggerFactory.getLogger(HawkCatcher.class);
    private static int errorCount = 0;
    private static int warningCount = 0;

    /**
     * Initializes the global error handler and logging mechanisms.
     * Sets up uncaught exception handler for the entire JVM.
     */
    @PostConstruct
    public void initialize() {
        Thread.setDefaultUncaughtExceptionHandler((thread, exception) -> {
            logError("Uncaught exception in thread " + thread.getName() + ": " + exception.getMessage());
        });
    }

    /**
     * Logs error messages to the console and increments the error counter.
     *
     * @param message The error message to log.
     */
    public static void logError(String message) {
        errorCount++;
        logger.error("Error: " + message);
    }

    /**
     * Logs warning messages to the console and increments the warning counter.
     *
     * @param message The warning message to log.
     */
    public static void logWarning(String message) {
        warningCount++;
        logger.warn("Warning: " + message);
    }

    /**
     * Reports the current status of errors and warnings.
     */
    public static void reportStatus() {
        String statusMessage = String.format("Current Status:%nErrors: %d%nWarnings: %d", errorCount, warningCount);
        logger.info(statusMessage);
    }
}