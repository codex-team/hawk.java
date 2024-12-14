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
     * Private constructor to initialize the Hawk instance.
     *
     * @param token the authentication token
     */
    private Hawk(String token) {
        this.token = token;
        this.integrationId = decodeToken(token);
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
     * Decodes the integration ID from a Base64-encoded token.
     *
     * @param token the encoded token
     * @return the integration ID
     */
    public static String decodeToken(String token) {
        try {
            String decodedJson = new String(Base64.getDecoder().decode(token));
            JSONObject json = new JSONObject(decodedJson);
            return json.getString("integrationId");
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid token: Unable to decode Base64 JSON.");
        }
    }

    /**
     * Sends an error or a custom message to the server based on the type of input.
     *
     * @param messageOrException Either a custom message or an exception to send.
     */
    public static void send(Object messageOrException) {
        Hawk hawkInstance = getInstance();

        String payload = buildPayload(hawkInstance, messageOrException);

        HawkHttpUtils.sendPostRequest(hawkInstance.getEndpointBase(), payload);
    }

    /**
     * Builds the payload for the server request based on the input type.
     *
     * @param hawkInstance the current Hawk instance
     * @param messageOrException the custom message or exception
     * @return the JSON payload as a string
     */
    private static String buildPayload(Hawk hawkInstance, Object messageOrException) {
        JSONObject payloadObj = new JSONObject();
        payloadObj.put("token", hawkInstance.getToken());
        payloadObj.put("catcherType", "errors/java");

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

        payloadObj.put("payload", payloadDetails);
        return payloadObj.toString();
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
