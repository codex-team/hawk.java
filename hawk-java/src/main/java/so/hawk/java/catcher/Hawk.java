package so.hawk.java.catcher;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Base64;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.*;
/**
 * Manages uncaught exception handling in the application.
 */
public class Hawk {
    private static volatile Hawk instance;
    private final CustomUncaughtExceptionHandler exceptionHandler;
    private String token;
    private String secret;
    private String integrationId;
    private static String endpointBase;

    /**
     * Returns the singleton instance of HawkCatcher.
     *
     * @return the singleton instance
     */

    /**
     * Initializes a new HawkCatcher instance.
     */
    private Hawk(String token){
        this.token = token;
        decodeToken(token);
        endpointBase = String.format("https://%s.k1.hawk.so", integrationId);
        this.exceptionHandler = new CustomUncaughtExceptionHandler();
    }

    // Initialize Hawk with a token (must be called first)
    public static synchronized void init(String token) {
        if (instance == null) {
            instance = new Hawk(token);
        }
        getInstance().exceptionHandler.enable();
    }

    // Get the Singleton instance
    private static Hawk getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Hawk is not initialized. Please call Hawk.init(token) before using.");
        }
        return instance;
    }
    /**
     *  Sets the custom handler as the default uncaught exception handler.
     */

    private void decodeToken(String token){
        try {
            String decodedJson = new String(Base64.getDecoder().decode(token));
            JSONObject json = new JSONObject(decodedJson);
            this.integrationId = json.getString("integrationId");
            this.secret = json.getString("secret");
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid token: Unable to decode Base64 JSON.");
        }
    }

    public static void logError(Exception e) {
        Hawk hawk = getInstance();
        String payload = new JSONObject()
            .put("token", hawk.token)
            .put("catcherType", "errors/java")
            .put("payload", new JSONObject()
                .put("title", e.toString())
                .put("backtrace", hawk.getStackTrace(e))
            )
            .toString();

        System.out.println(e.toString());


        hawk.sendPostRequest(payload);
        System.out.println("Send log");
    }

    public static void send(String message) {
        Hawk hawk = getInstance();
        String payload = new JSONObject()
            .put("token", hawk.token)
            .put("catcherType", "errors/java")
            .put("payload", new JSONObject()
                .put("title", message)
//                .put("backtrace", new JSONArray())) // Empty backtrace for custom messages
            )
            .toString();
        hawk.sendPostRequest(payload);
        System.out.println("Send succsess");
    }

    // Helper method to convert stack trace to a JSON array
    private JSONArray getStackTrace(Exception e) {
        JSONArray stackTraceArray = new JSONArray();
        for (StackTraceElement element : e.getStackTrace()) {
            stackTraceArray.put(element.toString());
        }
        return stackTraceArray;
    }

    // Read the server response
    private String readResponse(HttpURLConnection connection) {
        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (Exception e) {
            return "Unable to read response.";
        }
        return response.toString();
    }


    // Send a POST request
    private void sendPostRequest(String payload) {
        try {
            URL url = new URL(endpointBase);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            System.out.println(">>>> " + payload);

            System.out.println("first try");
            try (OutputStream os = connection.getOutputStream()) {
                os.write(payload.getBytes());
                os.flush();
            }
            System.out.println("second try");
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                System.err.println("Failed to log error. HTTP response code: " + responseCode);
            }
            String responseMessage = readResponse(connection);
            System.err.println("Server response: " + responseMessage);

        } catch (Exception ex) {
            System.err.println("Failed to send error: " + ex.getMessage());
        }
    }
}