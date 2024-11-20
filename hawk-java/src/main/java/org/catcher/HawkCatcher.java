package org.catcher;

/**
 * Manages uncaught exception handling in the application.
 */
public class HawkCatcher{
    private final CustomUncaughtExceptionHandler exceptionHandler;

    /**
     * Initializes a new HawkCatcher instance.
     */
    public HawkCatcher(){
        this.exceptionHandler = new CustomUncaughtExceptionHandler();
    }

    /**
     *  Sets the custom handler as the default uncaught exception handler.
     */
    public void init(){
        exceptionHandler.enable();
    }
}
