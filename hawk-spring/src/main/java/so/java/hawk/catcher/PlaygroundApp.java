package so.java.hawk.catcher;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * PlaygroundApp Class
 * <p>
 * The main application class for running HawkCatcher example.
 * Initializes the HawkCatcher and demonstrates logging errors, warnings, and reporting status.
 */
@SpringBootApplication
@ComponentScan(basePackages = {"so.java.hawk.catcher", "so.java.hawk"}) // Add the packages to scan
public class PlaygroundApp {

    private final HawkCatcher hawkCatcher;

    /**
     * Constructor for PlaygroundApp.
     *
     * @param hawkCatcher HawkCatcher instance
     */
    public PlaygroundApp(HawkCatcher hawkCatcher) {
        this.hawkCatcher = hawkCatcher;
    }

    /**
     * Main method to run the application.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(PlaygroundApp.class, args);
    }

    /**
     * CommandLineRunner bean to execute actions after application startup.
     * Demonstrates logging warnings, handling exceptions, and reporting status.
     *
     * @return CommandLineRunner instance
     */
    @Bean
    CommandLineRunner run() {
        return args -> {
            // Log a warning manually
            hawkCatcher.logWarning("This is a test warning.");

            // Run a critical operation and handle any exceptions
            try {
                performCriticalOperation();
            } catch (Exception e) {
                hawkCatcher.logError("Caught an exception in critical operation: " + e.getMessage());
            }

            // Report current error status to check if there were issues
            hawkCatcher.reportStatus();
        };
    }

    /**
     * Perform a critical operation (example: division by zero).
     * This will cause an exception to demonstrate error handling.
     */
    private void performCriticalOperation() {
        // Example error: division by zero
        int result = 10 / 0;
    }
}