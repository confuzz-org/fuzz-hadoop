package org.apache.hadoop.conf;

import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

import java.io.*;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import net.moznion.random.string.RandomStringGenerator;

public class ConfigurationGenerator extends Generator<Configuration> {

    private static String PARAM_EQUAL_MARK = "=";
    private static String PARAM_VALUE_SPLITOR = ";";

    /* Current Fuzzing Test Class Name; Set with -Dclass=XXX */
    private static String clzName = null;
    /* Currently Fuzzing Test Method Name; Set with -Dmethod=XXX */
    private static String methodName = null;

    /* File name that stores all parameter constrains (e.g., valid values) */
    private static String constrainFile = null;
    /* Mapping that keeps all parameter valid values supportted */
    private static Map<String, String> paramConstrainMapping = null;

    /* Mapping directory that stores all test-param mapping files. Set with -Dmapping.dir=XXX */
    private static String mappingDir = null;
    /* Mapping that let generator know which configuration parameter to fuzz */
    private static Map<String, String> curTestMapping = null;

    private static Writer out;

    /**
     * Constructor for Configuration Generator
     */
    public ConfigurationGenerator() throws IOException {
        super(Configuration.class);
        clzName = System.getProperty("class");
        methodName = System.getProperty("method");
        mappingDir = System.getProperty("mapping.dir", "mappingDir");
        constrainFile = System.getProperty("constrain.file", "constrain");
        curTestMapping = parseTestParam(clzName, methodName);
        paramConstrainMapping = parseParamConstrain();
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
        // Here should be a for loop to set all the configuration parameter that used in the set;
        // For now we use TestIdentityProviders#testPluggableIdentityProvider as an example, which
        // only sets one param: CommonConfigurationKeys.IPC_IDENTITY_PROVIDER_KEY

        // conf.set(CommonConfigurationKeys.IPC_IDENTITY_PROVIDER_KEY, random.nextBytes(100).toString());
        // conf.setInt("fs.ftp.host.port", random.nextInt());

        for (Map.Entry<String, String> entry : curTestMapping.entrySet()) {
            if (!isNullOrEmpty(entry.getValue())) {
                try {
                    String randomValue = randomValue(entry.getKey(), entry.getValue(), random);
                    conf.set(entry.getKey(), randomValue);
                } catch (Exception e) {
                    System.out.println(" Configuration Name: " + entry.getKey() + " value " + entry.getValue() + " Exception:");
                    e.printStackTrace();
                    continue;
                }
            }
        }
	    return conf;
    }
 
    /**
     * Return a random value based on the type of @param value
     * @param value
     * @return
     */
    private static String randomValue(String name, String value, SourceOfRandomness random) throws IOException {
        // TODO: Next to find a way to randomly generate string that we don't know
        // Some parameter may only be able to fit into such values
        if (paramHasConstrains(name)) {
            RandomStringGenerator randomStringGenerator = new RandomStringGenerator(random.toJDKRandom());
            String returnStr = randomStringGenerator.generateByRegex(paramConstrainMapping.get(name));
            return returnStr;
        }
        if (isBoolean(value)) {
            return String.valueOf(random.nextBoolean());
        } else if (isInteger(value)) {
            return String.valueOf(random.nextInt());
        } else if (isFloat(value)) {
            return String.valueOf(random.nextFloat());
        } 
        // for now we only fuzz numeric and boolean configuration parameters.
        String returnStr = String.valueOf(random.nextBytes(10));
        return returnStr;
    }


    private static boolean paramHasConstrains(String name) {
        return paramConstrainMapping.containsKey(name);
    }

    private static Map<String, String> parseParamConstrain() throws IOException {
        Map<String, String> result = new HashMap<String, String>();
        File file = Paths.get(mappingDir, constrainFile).toFile();
        if (!file.exists() || !file.isFile()){
            throw new IOException("Unable to read file: " + file.getPath());
        }
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        int index;
        while ((line = br.readLine()) != null) {
            index = line.indexOf(PARAM_EQUAL_MARK);
            if (index != -1) {
                String name = line.substring(0, index).trim();
                /* Only continue parsing when this parameter is used by current fuzzing test */
                if (curTestMapping.containsKey(name)) {
                    String value = line.substring(index + 1).trim();
                    result.put(name, value);
                }
            }
        }
        br.close();
        return result;
    }

    /**
     * Read configuration parameters and their exercised value in className#methodName
     * @param className
     * @param methodName
     * @return
     * @throws IOException
     */
    private static Map<String, String> parseTestParam(String className, String methodName) throws IOException {
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
        int index;
        while ((line = br.readLine()) != null) {
            index = line.indexOf(PARAM_EQUAL_MARK);
            if (index != -1) {
                String name = line.substring(0, index - 1).trim();
                String value = line.substring(index + 1).trim();
                mapping.put(name, value);
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
        return value.equals("null") || value == null || value.equals("");
    }

    /** For Internal test */
    public static void printMap(Map<String, List<String>> map) {
        System.out.println("In printing!!");
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            String name = entry.getKey();
            String value = "";
            for (String s : entry.getValue()) {
                value = value + ";" + s;
            }
            System.out.println(name + "=" + value);
        }
    }


    public static void main(String[] args) {
        Path file = Paths.get("/home/swang516/xlab/test_file");
        try {
            ConfigurationGenerator cg = new ConfigurationGenerator();
            // SourceOfRandomness random = new SourceOfRandomness(new Random());
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
