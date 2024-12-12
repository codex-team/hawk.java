package so.hawk.java.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Handles exceptions globally for a Spring Boot application.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private final ExceptionCallback callback;

    /**
     * Constructs a GlobalExceptionHandler with the given callback.
     *
     * @param callback the callback to invoke on exceptions
     */
    public GlobalExceptionHandler(ExceptionCallback callback) {
        this.callback = callback;
    }

    /**
     * Handles general exceptions and invokes the callback.
     *
     * @param exception the exception to handle
     */
    @ExceptionHandler(Exception.class)
    public void handleException(Exception exception) {
        System.err.printf(
                "Handling exception of type: %s%nMessage: %s%n",
                exception.getClass().getSimpleName(),
                exception.getMessage()
        );
        callback.onException(exception);
    }

    /**
     * Interface for custom exception handling.
     * <p>
     * Implement this interface to define how exceptions should be processed.
     */
    public interface ExceptionCallback {

        /**
         * Handles the given exception.
         *
         * @param exception the exception to handle
         */
        void onException(Throwable exception);
    }
}

/**
 * Default implementation of {@link GlobalExceptionHandler.ExceptionCallback} for logging exceptions.
 * Logs the exception's type and message to the standard error output.
 */
@Component
class DefaultExceptionCallback implements GlobalExceptionHandler.ExceptionCallback {

    /**
     * Logs the details of the caught exception.
     *
     * @param exception the exception to log, including its type and message
     */
    @Override
    public void onException(Throwable exception) {
        System.err.printf(
                "Caught exception of type: %s%nDetails: %s%n",
                exception.getClass().getSimpleName(),
                exception.getMessage()
        );
    }
}
