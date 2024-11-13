package org.playground;
import org.catcher.HawkCatcher;

public class PlaygroundApp {

    public static void main(String[] args) {
        HawkCatcher hawkCatcher = new HawkCatcher();
        hawkCatcher.init();

        throw new RuntimeException("Test Exception");
    }
}

