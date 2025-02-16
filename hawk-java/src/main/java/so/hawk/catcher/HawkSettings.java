package so.hawk.catcher;

import org.json.JSONObject;

/**
 * Represents the settings object for Hawk initialization.
 */
public class HawkSettings {
    /**
     * Authentication token for error reporting.
     */
    private String token;

    /**
     * Context data for additional information.
     */
    private JSONObject context;

    /**
     * Callback to modify payload before sending.
     */
    private BeforeSendCallback beforeSend;

    /**
     * User information related to errors.
     */
    private JSONObject user;

    /**
     * Constructor with mandatory token.
     *
     * @param defaultToken the default token
     */
    public HawkSettings(String defaultToken) {
        this.token = defaultToken;
        this.context = new JSONObject();
        this.user = new JSONObject();
    }

    /**
     * Sets the authentication token.
     *
     * @param token the authentication token
     * @return the HawkSettings instance
     */
    public HawkSettings setToken(String token) {
        this.token = token;
        return this;
    }

    /**
     * Sets a key-value pair in the context JSON object.
     *
     * @param key   the key to set
     * @param value the value to set
     * @return the HawkSettings instance
     */
    public HawkSettings setContext(String key, Object value) {
        this.context.put(key, value);
        return this;
    }

    /**
     * Sets the beforeSend callback.
     *
     * @param beforeSend the callback to execute before sending an event
     * @return the HawkSettings instance
     */
    public HawkSettings setBeforeSend(BeforeSendCallback beforeSend) {
        this.beforeSend = beforeSend;
        return this;
    }

    /**
     * Sets user information.
     *
     * @param key   the key to set
     * @param value the value to set
     * @return the HawkSettings instance
     */
    public HawkSettings setUser(String key, Object value) {
        this.user.put(key, value);
        return this;
    }

    /**
     * Gets the authentication token.
     *
     * @return the token
     */
    public String getToken() {
        return token;
    }

    /**
     * Gets the context data.
     *
     * @return the context
     */
    public JSONObject getContext() {
        return context;
    }

    /**
     * Gets the beforeSend callback.
     *
     * @return the beforeSend callback
     */
    public BeforeSendCallback getBeforeSend() {
        return beforeSend;
    }

    /**
     * Gets the user information.
     *
     * @return the user information
     */
    public JSONObject getUser() {
        return user;
    }
}