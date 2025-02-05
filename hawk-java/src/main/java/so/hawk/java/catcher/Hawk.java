package so.hawk.java.catcher;

import java.util.Base64;
import org.json.JSONObject;

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
     * String with catcher type.
     */
    private static final String catcherType = "errors/java";

    /**
     * Context data provided by the user.
     */
    private final JSONObject context;

    /**
     * Sends an error or a custom message to the server based on the type of input.
     *
     * @param messageOrException Either a custom message or an exception to send.
     */
    public static void send(Object messageOrException) {
        Hawk hawkInstance = getInstance();

        String payload = composeEvent(hawkInstance, messageOrException);

        HawkHttpUtils.sendPostRequest(hawkInstance.getEndpointBase(), payload);
    }

    /**
     * Sets a key-value pair in the context JSON object.
     *
     * @param key   the key to set
     * @param value the value to set
     */
    public static void setContext(String key, Object value) {
        getInstance().context.put(key, value);
    }

    /**
     * Private constructor to initialize the Hawk instance.
     *
     * @param token the authentication token
     * @param context the additional context data
     */
    private Hawk(String token, JSONObject context) {
        this.token = token;
        this.integrationId = extractIntegrationIdFromToken(token);
        this.endpointBase = String.format("https://%s.k1.hawk.so", integrationId);
        this.exceptionHandler = new CustomUncaughtExceptionHandler();
        this.context = context != null ? context : new JSONObject();
    }

    /**
     * Initializes the Hawk instance with the given token and context.
     *
     * @param token the authentication token
     * @param context the additional context data
     */
    public static synchronized void init(String token, JSONObject context) {
        if (instance == null) {
            instance = new Hawk(token, context);
        }
        getInstance().exceptionHandler.enable();
    }

    /**
     * Overloaded method to initialize the Hawk instance with only the token.
     *
     * @param token the authentication token
     */
    public static synchronized void init(String token) {
        init(token, null);
    }

    /**
     * Retrieves the singleton Hawk instance.
     *
     * @return the Hawk instance
     * @throws IllegalStateException if Hawk is not initialized
     */
    private static Hawk getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Hawk is not initialized. Please call Hawk.init(token, context) before using.");
        }
        return instance;
    }

    /**
     * Decodes the integration ID from a Base64-encoded token.
     *
     * @param token the encoded token
     * @return the integration ID
     */
    private static String extractIntegrationIdFromToken(String token) {
        try {
            String decodedJson = new String(Base64.getDecoder().decode(token));
            JSONObject json = new JSONObject(decodedJson);
            return json.getString("integrationId");
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid token: Unable to decode Base64 JSON.");
        }
    }

    /**
     * Builds the payload for the server request based on the input type.
     *
     * @param hawkInstance the current Hawk instance
     * @param messageOrException the custom message or exception
     * @return the JSON payload as a string
     */
    private static String composeEvent(Hawk hawkInstance, Object messageOrException) {
        JSONObject event = new JSONObject();
        event.put("token", hawkInstance.getToken());
        event.put("catcherType", catcherType);

        JSONObject payloadDetails = new JSONObject();

        if (messageOrException instanceof Exception) {
            Exception e = (Exception) messageOrException;
            payloadDetails.put("title", e.toString());
        } else if (messageOrException instanceof String) {
            String message = (String) messageOrException;
            payloadDetails.put("title", message);
        } else {
            throw new IllegalArgumentException("Invalid argument type. Expected String or Exception.");
        }

        payloadDetails.put("context", hawkInstance.context);

        event.put("payload", payloadDetails);
        return event.toString();
    }

    /**
     * Retrieves the token used by this Hawk instance.
     *
     * @return the authentication token
     */
    private String getToken() {
        return token;
    }

    /**
     * Retrieves the base endpoint URL for this Hawk instance.
     *
     * @return the endpoint base URL
     */
    private String getEndpointBase() {
        return endpointBase;
    }
}