package org.playground;
import org.catcher.HawkCatcher;

import java.util.logging.Handler;

public class PlaygroundApp {

    public static void main(String[] args) {
        HawkCatcher hawkCatcher = new HawkCatcher();
        hawkCatcher.init();

        throw new RuntimeException("Test Exception");
    }

    private static void performCriticalOperation() throws Exception {
        // Example error: division by zero
        int result = 10 / 0;
    }
}

