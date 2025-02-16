package so.hawk.catcher;

import org.json.JSONObject;

/**
 * Interface for beforeSend callback.
 */
@FunctionalInterface
public interface BeforeSendCallback {
    /**
     * Executes before sending an event.
     *
     * @param payload the event payload
     * @return the modified payload or null to prevent sending
     */
    JSONObject onBeforeSend(JSONObject payload);
}
