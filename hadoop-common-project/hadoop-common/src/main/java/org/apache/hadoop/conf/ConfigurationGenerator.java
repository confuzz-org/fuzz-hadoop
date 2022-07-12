package org.apache.hadoop.conf;

import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import org.apache.hadoop.fs.CommonConfigurationKeys;

public class ConfigurationGenerator extends Generator<Configuration> {

    private static String clzName = null;

    /**
     * Constructor for Configuration Generator
     */
    public ConfigurationGenerator() {
        super(Configuration.class);
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

        // Here should be a for loop to set all the configuration parameter that used in the set;
        // For now we use TestIdentityProviders#testPluggableIdentityProvider as an example, which
        // only sets one param: CommonConfigurationKeys.IPC_IDENTITY_PROVIDER_KEY

        conf.set(CommonConfigurationKeys.IPC_IDENTITY_PROVIDER_KEY, random.nextBytes(100).toString());
        return conf;
    }
}
