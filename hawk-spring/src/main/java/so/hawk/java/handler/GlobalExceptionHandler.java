package so.hawk.java.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Custom global exception handler for Spring Boot applications.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles RuntimeException and returns a custom HTTP response.
     *
     * @param exception the exception to handle
     * @return a ResponseEntity with error details
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException exception) {
        System.out.printf("Handling RuntimeException: %s%n", exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("A RuntimeException occurred: " + exception.getMessage());
    }

    /**
     * Handles IllegalArgumentException and returns a custom HTTP response.
     *
     * @param exception the exception to handle
     * @return a ResponseEntity with error details
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException exception) {
        System.out.printf("Handling IllegalArgumentException: %s%n", exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("An IllegalArgumentException occurred: " + exception.getMessage());
    }
}