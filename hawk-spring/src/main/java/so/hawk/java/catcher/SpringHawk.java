package so.hawk.java.catcher;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Manages uncaught exception handling for Spring Boot applications.
 */
public class SpringHawk extends CustomUncaughtExceptionHandler implements ApplicationContextAware {

    private static volatile SpringHawk instance;
    private ApplicationContext applicationContext;

    /**
     * Returns the singleton instance of SpringHawk.
     *
     * @return the singleton instance
     */
    private static SpringHawk getInstance() {
        if (instance == null) {
            synchronized (SpringHawk.class) {
                if (instance == null) {
                    instance = new SpringHawk();
                }
            }
        }
        return instance;
    }

    /**
     * Initializes a new SpringHawk instance.
     */
    private SpringHawk() {
        super(); // Call the constructor of CustomUncaughtExceptionHandler
    }

    /**
     * Sets the global exception handler for the application.
     */
    public static void init() {
        getInstance().enable(); // Reuse the enable method from CustomUncaughtExceptionHandler
    }

    /**
     * Sets the Spring Application Context.
     *
     * @param applicationContext the application context
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Logs an exception using Spring's logging mechanisms.
     *
     * @param exception the exception to log
     */
    public void logException(Throwable exception) {
        if (applicationContext != null) {
            System.out.println("Exception logged within Spring context: " + exception.getMessage());
            exception.printStackTrace();
        } else {
            System.out.println("ApplicationContext is not set. Logging exception as usual.");
            exception.printStackTrace();
        }
    }
}