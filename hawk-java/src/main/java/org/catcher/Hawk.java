package org.catcher;

/**
 * Manages uncaught exception handling in the application.
 */
public class Hawk {
    private static volatile Hawk instance;
    private final CustomUncaughtExceptionHandler exceptionHandler;

    /**
     * Returns the singleton instance of HawkCatcher.
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
     * Initializes a new HawkCatcher instance.
     */
    private Hawk(){
        this.exceptionHandler = new CustomUncaughtExceptionHandler();
    }

    /**
     *  Sets the custom handler as the default uncaught exception handler.
     */
    public static void init(){
        getInstance().exceptionHandler.enable();
    }
}
