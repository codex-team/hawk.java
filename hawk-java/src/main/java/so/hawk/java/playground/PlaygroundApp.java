package so.hawk.java.playground;

import so.hawk.java.catcher.Hawk;

/**
 * PlaygroundApp demonstrates the use of HawkCatcher for handling uncaught exceptions.
 */
public class PlaygroundApp {
    public static String integrationtoken = "PASTE_YOUR_TOKEN";

    /**
     * The main method initializes the HawkCatcher, runs test scenarios and sends custom error.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {

        Hawk.init(PlaygroundApp.integrationtoken);

        Hawk.setContext("application", "PlaygroundApp");
        Hawk.setContext("version", "1.0.0");
        Hawk.setContext("environment", "development");

        Hawk.send("I love Hawk so muchdfjkhdsfhds ");

        runTestScenarios();
    }

    /**
     * Runs various test scenarios that throw different types of exceptions.
     */
    private static void runTestScenarios() {
        Thread scenario1 = new Thread(() -> {
            throw new RuntimeException("Test Exception from Scenario 1");
        });
        scenario1.start();

        /**
         * This will throw ArrayOutOfBoundsException
         */
        Thread scenario2 = new Thread(() -> {
            int[] arr = new int[5];
            System.out.println(arr[10]);
        });
        scenario2.start();

        /**
         * This will throw NullPointerException
         */
        Thread scenario3 = new Thread(() -> {
            String str = null;
            System.out.println(str.length());
        });
        scenario3.start();

        Thread scenario4 = new Thread(() -> {
            throw new IllegalArgumentException("Illegal Argument Exception from Scenario 4");
        });
        scenario4.start();

        Thread scenario5 = new Thread(() -> {
            try {
                throw new Exception("Checked Exception from Scenario 5");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        scenario5.start();
    }
}