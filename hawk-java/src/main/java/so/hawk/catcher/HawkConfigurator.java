package so.hawk.catcher;

/**
 * Functional interface for configuring Hawk settings.
 */
@FunctionalInterface
public interface HawkConfigurator {
    /**
     * Configures the given HawkSettings object.
     *
     * @param settings the settings to configure
     */
    void configure(HawkSettings settings);
}
