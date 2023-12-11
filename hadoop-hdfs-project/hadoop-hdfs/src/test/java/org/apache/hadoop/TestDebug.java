package org.apache.hadoop;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.junit.Test;
import org.apache.hadoop.conf.Configuration;

public class TestDebug {
    @Test
    public void test(/*@From(ConfigurationGenerator.class) Configuration generatedConfig*/) throws Exception {
        HdfsConfiguration conf = new HdfsConfiguration();
        int count = 0;
        String str = conf.get("dfs.http.policy");
        System.out.println(str);
        if (str.equals("HTTP_ONLY")) {
            System.out.println("HTTP_ONLY");
            count ++;
        } else if (str.equals("HTTP_AND_HTTPS")) {
            System.out.println("HTTP_AND_HTTPS");
            conf.set("fake-config1","15");
            count --;
            throw new Exception("Fake Bug HTTP_AND_HTTPS");
        } else {
            System.out.println(str);
        }
    }

    @Test
    public void testECFuzzFail() {
        Configuration conf = new Configuration();
        String ms = conf.get("dfs.balancer.block-move.timeout");
        if (ms != null && ms.equals("4294967295")) {
            throw new RuntimeException("Fake Bug");
        }
    }
}
