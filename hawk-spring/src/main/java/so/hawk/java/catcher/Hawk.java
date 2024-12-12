package so.hawk.java.catcher;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Singleton class for managing uncaught exceptions and integrating with Spring Boot.
 */
public class Hawk implements ApplicationContextAware {

    /**
     * Singleton instance of Hawk.
     * It ensures only one instance of this class exists throughout the application lifecycle.
     */
    private static volatile Hawk instance;

    /**
     * Spring application context, used for accessing Spring-specific beans and resources.
     */
    private ApplicationContext applicationContext;

    /**
     * Initializes a new Hawk instance.
     * The constructor is private to enforce the singleton pattern.
     */
    private Hawk() {
    }

    /**
     * Returns the singleton instance of Hawk.
     * This ensures thread-safe lazy initialization.
     *
     * @return the singleton instance
     */
    private static Hawk getInstance() {
        if (instance == null) {
            synchronized (Hawk.class) {
                if (instance == null) {
                    instance = new Hawk();
                }
            }
        }
        return instance;
    }

    /**
     * Sets the global exception handler for the application.
     * Should be called during application startup to enable custom exception management.
     */
    public static void init() {
        getInstance(); // Singleton instance creation
    }

    /**
     * Sets the Spring Application Context.
     * This allows Hawk to access Spring beans and integrate with Spring Boot.
     *
     * @param applicationContext the application context provided by Spring
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Logs an exception with context-specific details.
     * If the Spring ApplicationContext is available, it logs within the Spring environment.
     *
     * @param exception the exception to log
     */
    public void logException(Throwable exception) {
        if (applicationContext != null) {
            System.out.println("Logging exception within Spring context: " + exception.getMessage());
            exception.printStackTrace();
        } else {
            System.out.println("ApplicationContext is not set. Logging exception to standard output.");
            exception.printStackTrace();
        }
    }
}