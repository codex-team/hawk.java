package so.hawk.java.catcher;

/**
 * Custom handler for uncaught exceptions in threads.
 */
public class CustomUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    private final Thread.UncaughtExceptionHandler defaultHandler;

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
//        System.out.printf("Exception in thread %s: %s\n", t.getName(), e.getMessage());
//        e.printStackTrace();

        Hawk.logError((Exception) e);

        if (defaultHandler != null) {
            defaultHandler.uncaughtException(t, e);
        }
    }
    /**
     * Enables this handler as the default uncaught exception handler.
     */
    public void enable() {
        Thread.setDefaultUncaughtExceptionHandler(this);
    }
}