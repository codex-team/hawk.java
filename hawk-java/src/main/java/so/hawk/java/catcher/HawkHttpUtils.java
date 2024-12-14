package so.hawk.java.catcher;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HawkHttpUtils {

    /**
     * Reads the server's response from the connection.
     *
     * @param connection the HTTP connection
     * @return the server's response as a string
     */
    static String readResponse(HttpURLConnection connection) {
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
    static void sendPostRequest(String endpointBase, String payload) {
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
