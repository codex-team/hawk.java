package so.hawk.java;

import so.hawk.java.catcher.SpringHawk;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * SpringPlaygroundApp demonstrates the use of SpringHawk for handling uncaught exceptions in a Spring Boot application.
 */
@SpringBootApplication
public class SpringPlaygroundApp {

    /**
     * The main method initializes SpringHawk and starts the Spring Boot application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringHawk.init();
        SpringApplication.run(SpringPlaygroundApp.class, args);
    }
}

/**
 * Controller that defines endpoints to trigger various exceptions for testing purposes.
 */
@RestController
@RequestMapping("/test")
class ExceptionTestController {

    /**
     * Triggers a RuntimeException.
     *
     * @return a message indicating the exception type
     */
    @GetMapping("/runtime")
    public String triggerRuntimeException() {
        throw new RuntimeException("Test RuntimeException");
    }

    /**
     * Triggers an ArrayIndexOutOfBoundsException.
     *
     * @return a message indicating the exception type
     */
    @GetMapping("/array")
    public String triggerArrayIndexOutOfBoundsException() {
        int[] arr = new int[5];
        return String.valueOf(arr[10]); // This will throw ArrayIndexOutOfBoundsException
    }

    /**
     * Triggers a NullPointerException.
     *
     * @return a message indicating the exception type
     */
    @GetMapping("/null")
    public String triggerNullPointerException() {
        String str = null;
        return str.toString(); // This will throw NullPointerException
    }

    /**
     * Triggers an IllegalArgumentException.
     *
     * @return a message indicating the exception type
     */
    @GetMapping("/illegal-argument")
    public String triggerIllegalArgumentException() {
        throw new IllegalArgumentException("Test IllegalArgumentException");
    }

    /**
     * Triggers a checked Exception wrapped in a RuntimeException.
     *
     * @return a message indicating the exception type
     */
    @GetMapping("/checked")
    public String triggerCheckedException() {
        try {
            throw new Exception("Test Checked Exception");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
