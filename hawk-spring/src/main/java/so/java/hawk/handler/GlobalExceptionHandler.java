package so.java.hawk.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import so.java.hawk.catcher.HawkCatcher;

/**
 * GlobalExceptionHandler Class
 *
 * Handles global exceptions for the application. It catches any unhandled exceptions,
 * logs them using HawkCatcher, and returns a generic error response.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private final HawkCatcher hawkCatcher;

    /**
     * Constructor for GlobalExceptionHandler.
     *
     * @param hawkCatcher HawkCatcher instance
     */
    public GlobalExceptionHandler(HawkCatcher hawkCatcher) {
        this.hawkCatcher = hawkCatcher;
    }

    /**
     * Global exception handler for any Exception thrown within the application.
     * Logs the exception and sends an HTTP 500 response with the error message.
     *
     * @param exception The exception that was thrown
     * @return A ResponseEntity containing the error message and HTTP status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception exception) {
        hawkCatcher.logError("Handled exception: " + exception.getMessage());
        return new ResponseEntity<>("An error occurred: " + exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}