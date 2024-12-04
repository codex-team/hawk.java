package so.hawk.java.catcher;

/**
 * Manages uncaught exception handling in the application.
 */
public class Hawk {
    /**
     * Singleton instance of Hawk.
     */
    private static volatile Hawk instance;

    /**
     * Custom uncaught exception handler.
     */
    private final CustomUncaughtExceptionHandler exceptionHandler;

    /**
     * Authentication token used for error reporting.
     */
    private final String token;

    /**
     * Integration ID extracted from the token.
     */
    private final String integrationId;

    /**
     * Base endpoint for sending error reports.
     */
    private final String endpointBase;

    /**
     * Private constructor to initialize the Hawk instance.
     *
     * @param token the authentication token
     */
    private Hawk(String token) {
        this.token = token;
        this.integrationId = HawkHttpUtils.decodeToken(token);
        this.endpointBase = String.format("https://%s.k1.hawk.so", integrationId);
        this.exceptionHandler = new CustomUncaughtExceptionHandler();
    }

    /**
     * Initializes the Hawk instance with the given token.
     *
     * @param token the authentication token
     */
    public static synchronized void init(String token) {
        if (instance == null) {
            instance = new Hawk(token);
        }
        getInstance().exceptionHandler.enable();
    }

    /**
     * Retrieves the singleton Hawk instance.
     *
     * @return the Hawk instance
     * @throws IllegalStateException if Hawk is not initialized
     */
    private static Hawk getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Hawk is not initialized. Please call Hawk.init(token) before using.");
        }
        return instance;
    }

    /**
     * Sends an error report for the given exception.
     *
     * @param e the exception to report
     */
    public static void sendError(Exception e) {
        HawkHttpUtils.sendError(getInstance(), e);
    }

    /**
     * Sends a custom message to the server.
     *
     * @param message the message to send
     */
    public static void send(String message) {
        HawkHttpUtils.send(getInstance(), message);
    }

    /**
     * Retrieves the token used by this Hawk instance.
     *
     * @return the authentication token
     */
    public String getToken() {
        return token;
    }

    /**
     * Retrieves the base endpoint URL for this Hawk instance.
     *
     * @return the endpoint base URL
     */
    public String getEndpointBase() {
        return endpointBase;
    }
}
