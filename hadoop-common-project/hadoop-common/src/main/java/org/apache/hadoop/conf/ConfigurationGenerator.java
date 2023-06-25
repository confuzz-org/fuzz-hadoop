package org.apache.hadoop.conf;

import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import edu.illinois.confuzz.internal.ConfuzzGenerator;

import java.io.*;

public class ConfigurationGenerator extends Generator<Configuration> {
    private static Configuration generatedConf = null;
    private static String setMethodName = "generatorSet";
    private static Class<?>[] setParameterTypes = {String.class, String.class};
    /**
     * Constructor for Configuration Generator
     */
    public ConfigurationGenerator() throws IOException {
        super(Configuration.class);
    }

    public static Configuration getGeneratedConfig() {
        if (generatedConf == null) {
            return null;
        }
        return new Configuration(generatedConf);
    }

    /**
     * This method is invoked to generate a Configuration object
     * @param random
     * @param generationStatus
     * @return
     */
    @Override
    public Configuration generate(SourceOfRandomness random, GenerationStatus generationStatus) {
        Configuration conf = new Configuration(true);
        try {
            generatedConf = (Configuration) ConfuzzGenerator.generate(random, conf, conf.getClass(), setMethodName, setParameterTypes);
            return generatedConf;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
//    public Configuration generate(SourceOfRandomness random, GenerationStatus generationStatus) {
//        //Initialize a Configuration object
//        if (clzName == null || methodName == null) {
//            throw new RuntimeException("Must specify test class name and test method name!");
//        }
//        ConfigGenerator.debugPrint("Map size before freshMap = " + ConfigTracker.getMapSize());
//        curTestMapping = ConfigTracker.getConfigMap();
//
//        if (Boolean.getBoolean("preround")) {
//            ConfigGenerator.debugPrint("Return default configuration conf");
//            generatedConf = new Configuration(true);
//            return generatedConf;
//        }
//
//        Configuration conf = new Configuration(true);
//        // Directly return the default configuration if it's pre-round
//        // Otherwise the curTestMapping size should larger than 0 (if not then there is
//        // no configuration parameter need to be fuzzed)
//        if (curTestMapping.size() == 0) {
//            throw new IllegalArgumentException("No configuration parameter tracked from test " + clzName + "#" +
//                    methodName +". Make sure (1) Set -DconfigFuzz flag correctly; (2) the test exercises at least " +
//                    "one configuration parameter");
//        }
//
//        if (curTestMapping == null) {
//            throw new RuntimeException("Unable to get configuration mapping for current test: " + clzName + "#" +
//                    methodName);
//        }
//
//        // curTestMapping is a sorted TreeMap
//        ConfigTracker.clearGenerated();
//        for (Map.Entry<String, String> entry : curTestMapping.entrySet()) {
//            try {
//                String randomValue = ConfigGenerator.randomValue(entry.getKey(), entry.getValue(), random);
//                // Set the configuration parameter only if the random value is not null
//                if (randomValue != null) {
//                    conf.generatorSet(entry.getKey(), randomValue);
//                    ConfigGenerator.debugPrint("Setting conf " + entry.getKey() + " = " + randomValue);
//                }
//            } catch (Exception e) {
//                ConfigGenerator.debugPrint(" Configuration Name: " + entry.getKey() + " value " +
//                        entry.getValue() + " Exception:");
//                e.printStackTrace();
//            }
//        }
//        //ConfigTracker.freshMap();  // --> Comment out if we want to incrementally collect exercised config set.
//        ConfigTracker.clearSetConfigMap();
//        generatedConf = conf;
//	    return generatedConf;
//    }
}