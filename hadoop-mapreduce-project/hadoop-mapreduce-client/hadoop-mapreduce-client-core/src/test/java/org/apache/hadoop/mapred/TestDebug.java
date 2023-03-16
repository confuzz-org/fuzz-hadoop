package org.apache.hadoop.mapred;
//import org.apache.hadoop.mapred.JobConf;
import org.junit.Test;
import org.apache.hadoop.conf.Configuration;

public class TestDebug {
    @Test
    public void test(/*@From(ConfigurationGenerator.class) Configuration generatedConfig*/) throws Exception {
        JobConf conf = new JobConf();
        int count = 0;
        conf.set('test', '5')
        String str = conf.get("yarn.app.mapreduce.am.log.level");
        System.out.println(str);
        if (str.equals("OFF")) {
            System.out.println("OFF");
            count ++;
        } else if (str.equals("FATAL")) {
            System.out.println("FATAL");
            conf.set("fake-config1","15");
            count --;
            throw new Exception("Fake Bug FATAL");
        } else {
            System.out.println(str);
        }
    }
}
