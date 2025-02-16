package so.hawk.catcher;

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
            // Создаем новый объект настроек
            HawkSettings settings = new HawkSettings("default_token");

            // Применяем конфигурацию через лямбду
            configLambda.configure(settings);

            // Инициализируем Hawk с готовыми настройками
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

            payloadDetails.put("backtrace", getStackTraceWithSource(e));
        } else if (messageOrException instanceof String) {
            String message = (String) messageOrException;
            payloadDetails.put("title", message);
        } else {
            throw new IllegalArgumentException("Invalid argument type. Expected String or Exception.");
        }

        payloadDetails.put("context", hawkInstance.context);
        payloadDetails.put("user", hawkInstance.user);
        event.put("payload", payloadDetails);

        System.out.println("Composed event: " + event.toString(2));

        return event.toString();
    }

    private static JSONObject getStackTraceWithSource(Throwable throwable) {
        JSONObject backtrace = new JSONObject();
        StackTraceElement[] stackTrace = throwable.getStackTrace();

        JSONArray frames = new JSONArray();
        for (StackTraceElement element : stackTrace) {
            JSONObject frame = new JSONObject();
            frame.put("class", element.getClassName());
            frame.put("method", element.getMethodName());
            frame.put("file", element.getFileName());
            frame.put("line", element.getLineNumber());

            frame.put("source", getSourceCode(element));

            frames.put(frame);
        }

        backtrace.put("frames", frames);
        return backtrace;
    }

    private static String getSourceCode(StackTraceElement element) {
        try {
            String filePath = element.getFileName();
            int lineNumber = element.getLineNumber();

            java.nio.file.Path path = java.nio.file.Paths.get("src", filePath);
            if (!java.nio.file.Files.exists(path)) {
                return "Source code unavailable";
            }

            List<String> lines = java.nio.file.Files.readAllLines(path);

            int startLine = Math.max(0, lineNumber - 11);
            int endLine = Math.min(lines.size(), lineNumber + 9);

            StringBuilder sourceCode = new StringBuilder();
            for (int i = startLine; i < endLine; i++) {
                String line = lines.get(i).trim();
                sourceCode.append(String.format("%4d | %s%n", i + 1, line));
            }

            return sourceCode.toString();
        } catch (Exception e) {
            return "Source code unavailable";
        }
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