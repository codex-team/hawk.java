package so.hawk.catcher;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import org.json.JSONArray;
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
     * Callback to execute before sending an event.
     */
    private final BeforeSendCallback beforeSend;

    /**
     * User information.
     */
    private final JSONObject user;

    /**
     * Private constructor to initialize the Hawk instance with settings.
     *
     * @param settings the configuration settings
     */
    private Hawk(HawkSettings settings) {
        this.token = settings.getToken();
        this.integrationId = extractIntegrationIdFromToken(this.token);
        this.endpointBase = String.format("https://%s.k1.hawk.so", integrationId);
        this.exceptionHandler = new CustomUncaughtExceptionHandler();
        this.context = settings.getContext() != null ? settings.getContext() : new JSONObject();
        this.beforeSend = settings.getBeforeSend();
        this.user = settings.getUser();
    }

    /**
     * Sends an error or a custom message to the server based on the type of input.
     *
     * @param messageOrException Either a custom message or an exception to send.
     */
    public static void send(Object messageOrException) {
        Hawk hawkInstance = getInstance();
        String payload = composeEvent(hawkInstance, messageOrException);

        if (hawkInstance.beforeSend != null) {
            JSONObject jsonPayload = new JSONObject(payload);
            JSONObject modifiedPayload = hawkInstance.beforeSend.onBeforeSend(jsonPayload);
            if (modifiedPayload == null) {
                System.out.println("Event was prevented from being sent.");
                return;
            }
            payload = modifiedPayload.toString();
        }

        HawkHttpUtils.sendPostRequest(hawkInstance.getEndpointBase(), payload);
    }

    /**
     * Initializes the Hawk instance with a configuration lambda.
     *
     * @param configLambda the configuration lambda
     */
    public static synchronized void init(HawkConfigurator configLambda) {
        if (instance == null) {
            HawkSettings settings = new HawkSettings("default_token");

            configLambda.configure(settings);

            instance = new Hawk(settings);
            getInstance().exceptionHandler.enable();
        }
    }

    /**
     * Overloaded method to initialize the Hawk instance with only the token.
     *
     * @param token the authentication token
     */
    public static synchronized void init(String token) {
        init(config -> config.setToken(token));
    }

    /**
     * Retrieves the singleton Hawk instance.
     *
     * @return the Hawk instance
     * @throws IllegalStateException if Hawk is not initialized
     */
    private static Hawk getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Hawk is not initialized. Please call Hawk.init() before using.");
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

            payloadDetails.putOpt("type", e.getClass().getSimpleName());
            payloadDetails.putOpt("description", e.getMessage());

            payloadDetails.put("backtrace", getStackTraceWithSource(e));
        } else if (messageOrException instanceof String) {
            String message = (String) messageOrException;
            payloadDetails.put("title", message);
        } else {
            throw new IllegalArgumentException("Invalid argument type. Expected String or Exception.");
        }

        payloadDetails.put("context", hawkInstance.context);
        payloadDetails.put("user", hawkInstance.user);
        payloadDetails.putOpt("release", hawkInstance.context.optString("version"));
        payloadDetails.putOpt("addons", new JSONObject());

        event.put("payload", payloadDetails);

        System.out.println("Composed event: " + event.toString(2));

        return event.toString();
    }

    /**
     * Creates a JSON array representing the stack trace (backtrace) based on the provided throwable.
     * Each element in the array represents a single frame of the stack trace and includes information
     * about the file, line number, method name, and a snippet of the source code.
     *
     * @param throwable The exception or error for which the backtrace is being generated.
     * @return A JSONArray containing the backtrace details for each stack frame.
     */
    private static JSONArray getStackTraceWithSource(Throwable throwable) {
        JSONArray backtrace = new JSONArray();

        for (StackTraceElement element : throwable.getStackTrace()) {
            JSONObject frame = new JSONObject();

            frame.put("file", element.getFileName() != null ? element.getFileName() : "Unknown file");
            frame.put("line", element.getLineNumber());
            frame.put("column", 0); // Column номер обычно недоступен в Java
            frame.putOpt("function", element.getMethodName());

            String sourceCode = getSourceCode(element);
            if (sourceCode.equals("Source code unavailable")) {
                frame.put("sourceCode", (Object) null);
            } else {
                JSONArray sourceCodeArray = new JSONArray();
                String[] lines = sourceCode.split("\n");
                for (String line : lines) {
                    String[] parts = line.split("\\|", 2);
                    if (parts.length == 2) {
                        try {
                            int lineNum = Integer.parseInt(parts[0].trim());
                            String content = parts[1];
                            JSONObject lineObj = new JSONObject();
                            lineObj.put("line", lineNum);
                            lineObj.put("content", content);
                            sourceCodeArray.put(lineObj);
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }
                frame.put("sourceCode", sourceCodeArray);
            }

            backtrace.put(frame);
        }

        return backtrace;
    }

    /**
     * Attempts to retrieve a snippet of the source code for the given stack trace element.
     * Searches for the file in predefined directories and reads the lines around the specified line number.
     *
     * @param element The stack trace element for which the source code is being retrieved.
     * @return A string representation of the source code snippet or "Source code unavailable" if the file cannot be found.
     */
    private static String getSourceCode(StackTraceElement element) {
        try {
            String fileName = element.getFileName();
            if (fileName == null || fileName.isEmpty()) {
                return "Source code unavailable";
            }

            List<String> searchPaths = Arrays.asList(
                    "src/main/java",
                    "src"
            );

            for (String basePath : searchPaths) {
                String fullFilePath = constructFullPath(basePath, element.getClassName(), fileName);
                java.nio.file.Path fullPath = java.nio.file.Paths.get(fullFilePath);

                if (java.nio.file.Files.exists(fullPath)) {
                    System.out.println("File found: " + fullPath);
                    return readFileContent(fullPath, element.getLineNumber());
                } else {
                    System.out.println("File not found: " + fullPath);
                }
            }

            return "Source code unavailable";
        } catch (Exception e) {
            System.err.println("Error while reading source code: " + e.getMessage());
            return "Source code unavailable";
        }
    }

    /**
     * Constructs the full path to the source file based on the base directory, class name, and file name.
     *
     * @param basePath   The base directory where the source file is expected to be located.
     * @param className  The fully qualified name of the class associated with the stack trace element.
     * @param fileName   The name of the source file.
     * @return The full path to the source file.
     */
    private static String constructFullPath(String basePath, String className, String fileName) {

        String packagePath = className.replace('.', '/');
        return basePath + "/" + packagePath + "." + getExtension(fileName);
    }

    /**
     * Extracts the file extension from the given file name.
     *
     * @param fileName The name of the file.
     * @return The file extension (e.g., "java") or an empty string if no extension is present.
     */
    private static String getExtension(String fileName) {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf('.') + 1);
        }
        return "";
    }

    /**
     * Reads the content of the source file and extracts a snippet around the specified line number.
     *
     * @param path       The full path to the source file.
     * @param lineNumber The line number around which the snippet is extracted.
     * @return A formatted string containing the source code snippet with line numbers.
     * @throws Exception If an error occurs while reading the file.
     */
    private static String readFileContent(java.nio.file.Path path, int lineNumber) throws Exception {
        List<String> lines = java.nio.file.Files.readAllLines(path);

        int startLine = Math.max(0, lineNumber - 11);
        int endLine = Math.min(lines.size(), lineNumber + 9);

        StringBuilder sourceCode = new StringBuilder();
        for (int i = startLine; i < endLine; i++) {
            sourceCode.append(String.format("%4d | %s%n", i + 1, lines.get(i)));
        }

        return sourceCode.toString();
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