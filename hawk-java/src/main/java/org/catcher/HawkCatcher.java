package org.catcher;

/**
 * Manages uncaught exception handling in the application.
 */
public class HawkCatcher{
    private static volatile HawkCatcher instance;
    private final CustomUncaughtExceptionHandler exceptionHandler;

    /**
     * Returns the singleton instance of HawkCatcher.
     *
     * @return the singleton instance
     */
    private static HawkCatcher getInstance() {
        if (instance == null) {
            synchronized (HawkCatcher.class) {
                if (instance == null) {
                    instance = new HawkCatcher();
                }
            }
        }
        return instance;
    }

    /**
     * Initializes a new HawkCatcher instance.
     */
    private HawkCatcher(){
        this.exceptionHandler = new CustomUncaughtExceptionHandler();
    }

    /**
     *  Sets the custom handler as the default uncaught exception handler.
     */
    public static void init(){
        getInstance().exceptionHandler.enable();
    }
}
