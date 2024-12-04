package so.hawk.java.catcher;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

public class HawkHttpUtils {

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
     * Sends an error message to the server.
     *
     * @param hawkInstance the Hawk instance to use
     * @param e            the exception to log
     */
    public static void sendError(Hawk hawkInstance, Exception e) {
      String payload = new JSONObject()
          .put("token", hawkInstance.getToken())
          .put("catcherType", "errors/java")
          .put("payload", new JSONObject()
              .put("title", e.toString())
          )
          .toString();

      System.out.println(e.toString());
      sendPostRequest(hawkInstance.getEndpointBase(), payload);
      System.out.println("Send log");
    }

    /**
     * Sends a custom message to the server.
     *
     * @param hawkInstance the Hawk instance to use
     * @param message       the custom message to send
     */
    public static void send(Hawk hawkInstance, String message) {
      String payload = new JSONObject()
          .put("token", hawkInstance.getToken())
          .put("catcherType", "errors/java")
          .put("payload", new JSONObject()
              .put("title", message)
          )
          .toString();

      sendPostRequest(hawkInstance.getEndpointBase(), payload);
      System.out.println("Send success");
    }

    /**
     * Reads the server's response from the connection.
     *
     * @param connection the HTTP connection
     * @return the server's response as a string
     */
    private static String readResponse(HttpURLConnection connection) {
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

    /**
     * Sends a POST request with the given payload to the specified endpoint.
     *
     * @param endpointBase the endpoint URL
     * @param payload      the JSON payload to send
     */
    private static void sendPostRequest(String endpointBase, String payload) {
      try {
        URL url = new URL(endpointBase);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        System.out.println(">>>> " + payload);

        try (OutputStream os = connection.getOutputStream()) {
          os.write(payload.getBytes());
          os.flush();
        }

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
