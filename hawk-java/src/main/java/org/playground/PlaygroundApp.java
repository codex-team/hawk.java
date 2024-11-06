package org.playground;
import org.catcher.HawkCatcher;

public class PlaygroundApp {

    public static void main(String[] args) {
        // Log a warning manually
        HawkCatcher.logWarning("This is a test warning.");

        // Run a critical operation
        try {
            performCriticalOperation();
        } catch (Exception e) {
            HawkCatcher.logError("Caught an exception in critical operation: " + e.getMessage());
        }

        // Report current error status to check if there were issues
        HawkCatcher.reportStatus();
    }

    private static void performCriticalOperation() throws Exception {
        // Example error: division by zero
        int result = 10 / 0;
    }
}

