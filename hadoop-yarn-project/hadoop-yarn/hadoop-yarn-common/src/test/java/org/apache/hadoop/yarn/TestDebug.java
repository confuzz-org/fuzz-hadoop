package org.apache.hadoop.yarn;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.apache.hadoop.conf.Configuration;

import java.io.IOException;
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

    @Test
    public void testWrappedException() throws IOException {
        try {
            Configuration conf = new Configuration();
            String policy = conf.get("yarn.http.policy", "HTTP_ONLY");
            String mask = conf.get("fs.permissions.umask-mode", "022");
            conf.get("yarn.scheduler.configuration.store.class");
            if (Objects.equals(policy, "HTTPS_ONLY")) {
                throw new IllegalArgumentException("Wrapped Exception");
            }
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Test
    public void testNewJson() {
        Configuration conf = new Configuration();
        String size = conf.get("dfs.namenode.fs-limits.max-directory-items", "300");
        String interval = conf.get("dfs.lock.suppress.warning.interval");
        String level = conf.get("yarn.app.mapreduce.am.log.level");
        System.out.println(size);
        System.out.println(interval);
        System.out.println(level);
        //String policy = conf.get("yarn.http.policy", "HTTP_ONLY");
        if (Integer.valueOf(size) <= 0 || Integer.valueOf(size) > 6400000) {
            Assert.assertTrue(false);
        } else {
            System.out.println("Test passed");
        }
    }
}
