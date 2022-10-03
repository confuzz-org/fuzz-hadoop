package org.apache.hadoop.conf;

import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import edu.berkeley.cs.jqf.fuzz.configfuzz.ConfigGenerator;
import edu.berkeley.cs.jqf.fuzz.configfuzz.ConfigTracker;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ConfigurationGenerator extends Generator<Configuration> {

    /* Current Fuzzing Test Class Name; Set with -Dclass=XXX */
    private static String clzName = null;
    /* Currently Fuzzing Test Method Name; Set with -Dmethod=XXX */
    private static String methodName = null;

    /* Mapping that let generator know which configuration parameter to fuzz */
    private static Map<String, String> curTestMapping = null;
    private static boolean debugEnabled = Boolean.getBoolean("generator.debug");

    private static boolean isReprod = Boolean.getBoolean("repro.info");
    /**
     * Constructor for Configuration Generator
     */
    public ConfigurationGenerator() throws IOException {
        super(Configuration.class);
        clzName = System.getProperty("class");
        methodName = System.getProperty("method");
        /* Use TreeMap to sort the configuration set to prevent ordering inconsistency;
           Initialize the mapping with all default configuration set for the first round */
        curTestMapping = ConfigTracker.getConfigMap();
    }

    /**
     * This method is invoked to generate a Configuration object
     * @param random
     * @param generationStatus
     * @return
     */
    @Override
    public Configuration generate(SourceOfRandomness random, GenerationStatus generationStatus) {
        //Initialize a Configuration object
        if (clzName == null || methodName == null) {
            throw new RuntimeException("Must specify test class name and test method name!");
        }
        ConfigGenerator.debugPrint("Map size before freshMap = " + ConfigTracker.getMapSize());
        curTestMapping = ConfigTracker.getConfigMap();
        Configuration conf = new Configuration();

        // Directly return the default configuration if it's pre-round
        // Otherwise the curTestMapping size should larger than 0 (if not then there is
        // no configuration parameter need to be fuzzed)
        if (Boolean.getBoolean("preround")) {
            ConfigGenerator.debugPrint("Return default configuration conf");
            return conf;
        } else if (curTestMapping.size() == 0) {
            throw new IllegalArgumentException("No configuration parameter tracked from test " + clzName + "#" +
                    methodName +". Make sure (1) Set -DconfigFuzz flag correctly; (2) the test exercises at least " +
                    "one configuration parameter");
        }

        if (curTestMapping == null) {
            throw new RuntimeException("Unable to get configuration mapping for current test: " + clzName + "#" +
                    methodName);
        }

        // curTestMapping is a sorted TreeMap
        for (Map.Entry<String, String> entry : curTestMapping.entrySet()) {
            if (!ConfigGenerator.isNullOrEmpty(entry.getValue())) {
                try {
                    String randomValue = ConfigGenerator.randomValue(entry.getKey(), entry.getValue(), random);
                    ConfigGenerator.debugPrint("Setting conf " + entry.getKey() + " = " + randomValue);
                    if (isReprod) {
                        ConfigGenerator.debugPrint("Setting conf " + entry.getKey() + " = " + randomValue);
                    }
                    conf.set(entry.getKey(), randomValue);
                } catch (Exception e) {
                    ConfigGenerator.debugPrint(" Configuration Name: " + entry.getKey() + " value " +
                            entry.getValue() + " Exception:");
                    e.printStackTrace();
                    continue;
                }
            }
        }
        ConfigTracker.freshMap();
	    return conf;
    }

    // Internal test
    public static void main(String[] args) {
        Path file = Paths.get("/home/swang516/xlab/test_file");
        try {
            //ConfigurationGenerator cg = new ConfigurationGenerator();
            //printMap(curTestMapping);
            // curTestMapping = readFileToMapping(file);
            // // for (Map.Entry<String, String> entry : curTestMapping.entrySet()) {
            // //     System.out.println(entry.getKey() + "=" + entry.getValue());
            // // }

            // for (Map.Entry<String, String> entry : curTestMapping.entrySet()) {
            //     if (!isNullOrEmpty(entry.getValue())) {
            //         System.out.println(entry.getKey() + "= old: " + entry.getValue() + " new: " + randomValue(entry.getKey(), entry.getValue(), random));
            //     }
            // }
        } catch(Exception e) {
           e.printStackTrace();
        }
    }
}
