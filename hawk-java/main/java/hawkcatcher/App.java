package hawkcatcher;

/**
 * Main class of the HawkCatcher application.
 * This class demonstrates setting up an exception handler that catches
 * uncaught exceptions and outputs information about them to the console.
 * It also includes the {@link #throwException()} method to test the exception handler.
 */
public class App {

    /**
     * Entry point of the application.
     * Sets a handler for all uncaught exceptions in the current thread and
     * calls the {@link #throwException()} method to demonstrate the handler's functionality.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {

        // Set a handler for all uncaught exceptions in the current thread
        Thread.setDefaultUncaughtExceptionHandler((thread, exception) -> {
            System.out.println("Uncaught exception in thread " + thread.getName() + ": " + exception.getMessage());
            exception.printStackTrace();
        });

        // Call a method that throws an exception
        throwException();
    }

    /**
     * Method to test the exception handler.
     * Throws a {@link RuntimeException} with a test message.
     */
    private static void throwException() {
        throw new RuntimeException("This is a test exception");
    }
}
