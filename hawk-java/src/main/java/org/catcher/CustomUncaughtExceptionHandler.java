package org.catcher;

/**
 * Custom handler for uncaught exceptions in threads.
 */
public class CustomUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    private Thread.UncaughtExceptionHandler defaultHandler;

  /**
   * Initializes the custom handler and stores the default handler.
   */
    public CustomUncaughtExceptionHandler() {
        this.defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

  /**
   * Handles uncaught exceptions by logging them and invoking the default handler.
   *
   * @param t the thread that threw the exception
   * @param e the thrown exception
   */
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        System.out.printf("Exception in thread %s: %s\n", t.getName(), e.getMessage());
        e.printStackTrace();

        if (defaultHandler != null) {
            defaultHandler.uncaughtException(t, e);
        }
    }
}