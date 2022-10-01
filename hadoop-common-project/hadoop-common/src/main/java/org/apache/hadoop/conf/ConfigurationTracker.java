package org.apache.hadoop.conf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.TreeMap;

// TODO: How do we know which mutation cause the new execution path?
// TODO: e.g., next round if we keep the change then the input set is correct; otherwise it may not be correct;
public class ConfigurationTracker {

    private static Map<String, String> configMap = new TreeMap<>();
    private String curTestClass;
    private String curTestName;
    private static boolean trackerLogEnabled = Boolean.getBoolean("tracker.log");
    private static final Logger LOG =
            LoggerFactory.getLogger(ConfigurationTracker.class);
    private static int counter = 0;

    public ConfigurationTracker(String curTestClass, String curTestName) {
        this.curTestClass = curTestClass;
        this.curTestName = curTestName;
    }

    public static Map<String, String> getInitConfigMap() {
        configMap = new TreeMap<>(new Configuration().getValByRegex(".*"));
        return configMap;
    }

    public static void track(String key, String value) {
        configMap.put(key, value);
        if (trackerLogEnabled) {
            LOG.info("Tracker: " + key + " = " + value);
        }
        counter++;
    }

    public static Map<String, String> getConfigMap() {
        if (configMap == null) {
            return new TreeMap<>();
        }
        return configMap;
    }

    public static Boolean freshMap() {
        configMap = new TreeMap<>();
        if (configMap.size() == 0) {
            return true;
        }
        return false;
    }

    public static int getMapSize() {
        return configMap.size();
    }

    // Helper
    private void printMap(Map<String, String> map) {

    }
}
