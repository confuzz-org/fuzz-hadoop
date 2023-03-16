package org.apache.hadoop.yarn;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.junit.Test;
import org.apache.hadoop.conf.Configuration;

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
}
