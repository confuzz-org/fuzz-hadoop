package org.apache.hadoop.yarn;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.junit.Test;
import org.apache.hadoop.conf.Configuration;

import java.util.Objects;

public class TestDebug {
    @Test
    public void test(/*@From(ConfigurationGenerator.class) Configuration generatedConfig*/) throws Exception {
        YarnConfiguration conf = new YarnConfiguration();
        int count = 0;
        String str = conf.get("yarn.http.policy");
        System.out.println(str);
        if (str.equals("HTTP_ONLY")) {
            System.out.println("HTTP_ONLY");
            count ++;
        } else if (str.equals("HTTPS_ONLY")) {
            System.out.println("HTTPS_ONLY");
            conf.set("fake-config1","15");
            count --;
            throw new Exception("Fake Bug HTTP_AND_HTTPS");
        } else {
            System.out.println(str);
        }
    }

    @Test
    public void testInjection() {
        Configuration conf = new Configuration();
        // check configuration() can inject yarn parameter successfully
        String policy = conf.get("yarn.http.policy", "HTTP_ONLY");
        if (Objects.equals(policy, "HTTPS_ONLY")) {
            System.out.println("Configuration injection works");
        }

        // check YarncConfiguration can inject yarn parameter successfully
        Configuration yarnConf = new YarnConfiguration();
        String yarnPolicy = yarnConf.get("yarn.http.policy", "HTTP_ONLY");
        if (Objects.equals(yarnPolicy, "HTTPS_ONLY")) {
            System.out.println("YarnConfiguration injection works");
        }

        // check Configuration can inject other module's parameter successfully
        Configuration otherConf = new Configuration();
        String mask = otherConf.get("fs.permissions.umask-mode", "022");
        if (!Objects.equals(mask, "022")) {
            System.out.println("other module's Configuration injection works");
        }
    }
}
