package org.apache.hadoop.conf;

import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Random;
import java.util.HashMap;

public class ConfigurationGenerator extends Generator<Configuration> {

    /* Current Fuzzing Test Class Name; Set with -Dclass=XXX */
    private static String clzName = null;
    /* Currently Fuzzing Test Method Name; Set with -Dmethod=XXX */
    private static String methodName = null;


    /* Mapping directory that stores all test-param mapping files. Set with -Dmapping.dir=XXX */
    private static String mappingDir = null;
    /* Mapping that let generator know which configuration parameter to fuzz */
    private static Map<String, String> curTestMapping = null;

    /**
     * Constructor for Configuration Generator
     */
    public ConfigurationGenerator() throws IOException {
        super(Configuration.class);
        clzName = System.getProperty("class");
        methodName = System.getProperty("method");
        mappingDir = System.getProperty("mapping.dir");
        curTestMapping = readTestParamMapping(clzName, methodName);
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
        Configuration conf = new Configuration();
        if (clzName == null || methodName == null) {
            throw new RuntimeException("Must specify test class name and test method name!");
        }
        if (curTestMapping == null) {
            throw new RuntimeException("Unable to get configuration mapping for current test: " + clzName + "#" +
                    methodName);
        }
        // System.out.println("Current generate value for: " + clzName + "#" + methodName);
        // Here should be a for loop to set all the configuration parameter that used in the set;
        // For now we use TestIdentityProviders#testPluggableIdentityProvider as an example, which
        // only sets one param: CommonConfigurationKeys.IPC_IDENTITY_PROVIDER_KEY

        // conf.set(CommonConfigurationKeys.IPC_IDENTITY_PROVIDER_KEY, random.nextBytes(100).toString());
        // conf.setInt("fs.ftp.host.port", random.nextInt());

        for (Map.Entry<String, String> entry : curTestMapping.entrySet()) {
            if (!isNullOrEmpty(entry.getValue())) {
                conf.set(entry.getKey(), randomValue(random, entry.getValue()));
            }
        }
	    return conf;
    }
 
    /**
     * Return a random value based on the type of @param value
     * @param value
     * @return
     */
    private static String randomValue(SourceOfRandomness random, String value) {
        // TODO: Next to find a way to randomly generate string that we don't know
        // Some parameter may only be able to fit into such values
        if (isBoolean(value)) {
            return String.valueOf(random.nextBoolean());
        } else if (isInteger(value)) {
            return String.valueOf(random.nextInt());
        } else if (isFloat(value)) {
            return String.valueOf(random.nextFloat());
        } 
        // for now we only fuzz numeric and boolean configuration parameters.
        return value;
    }

    /**
     * Read configuration parameters and their exercised value in className#methodName
     * @param className
     * @param methodName
     * @return
     * @throws IOException
     */
    private static Map<String, String> readTestParamMapping(String className, String methodName) throws IOException {
        /* Here Get Param Name and Param Value from file */
        if (mappingDir == null) {
            throw new RuntimeException("Unable to get test-parameter mapping directory");
        }
        Path mappingFilePath = Paths.get(mappingDir, className + "#" + methodName);
        return readFileToMapping(mappingFilePath);
    }

    private static Map<String, String> readFileToMapping(Path filePath) throws IOException {
        Map<String, String> mapping = new HashMap<>();
        File file = filePath.toFile();
        if (!file.exists() || !file.isFile()) {
            throw new IOException("Unable to read file: " + filePath);
        }
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null) {
            int index = line.indexOf("=");
            if (index != -1) {
                mapping.put(line.substring(0, index - 1).trim(), line.substring(index + 1).trim());
            }
        }
        br.close();
        return mapping;
    }

    /** Helper Functions */
    private static boolean isInteger(String value) {
        try {
            int i = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private static boolean isBoolean(String value) {
        String trimStr = value.toLowerCase().trim();
        if (trimStr.equals("true") || trimStr.equals("false")) {
            return true;
        }
        return false;
    }

    private static boolean isFloat(String value) {
        try {
            float f = Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private static boolean isNullOrEmpty(String value) {
        return value == null || value.equals("");
    }


    /** For Internal test */
    public static void main(String[] args) {
        Path file = Paths.get("/home/swang516/xlab/test_file");
        try {
            SourceOfRandomness random = new SourceOfRandomness(new Random());
            curTestMapping = readFileToMapping(file);
            // for (Map.Entry<String, String> entry : curTestMapping.entrySet()) {
            //     System.out.println(entry.getKey() + "=" + entry.getValue());
            // }

            for (Map.Entry<String, String> entry : curTestMapping.entrySet()) {
                if (!isNullOrEmpty(entry.getValue())) {
                    System.out.println(entry.getKey() + "= old: " + entry.getValue() + " new: " + randomValue(random, entry.getValue()));
                }
            }
        } catch(IOException e) {
           e.printStackTrace();
        }
    }
}
