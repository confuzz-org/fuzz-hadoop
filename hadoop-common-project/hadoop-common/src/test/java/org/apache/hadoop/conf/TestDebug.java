package org.apache.hadoop.conf;

import com.pholser.junit.quickcheck.From;
import org.junit.Test;


public class TestDebug {

    @Test
    public void test(/*@From(ConfigurationGenerator.class) Configuration generatedConfig*/) throws Exception {
        Configuration conf = new Configuration();
        int count = 0;
        String str = conf.get("fs.s3a.select.output.csv.quote.fields");
        //System.out.println(str);
        if (str.equals("always")) {
            System.out.println("always");
            count ++;
        } else if (str.equals("asneeded")) {
            System.out.println("asneeded");
	        conf.set("fake-config1","15");
            count --;
            throw new Exception("Fake Bug");
        } else {
            System.out.println(str);
        }
        //System.out.println("Conf Length : " + conf.size());
    }

    @Test
    public void test2() {
        Configuration conf = new Configuration();
        conf.set("fs.s3a.select.output.csv.quote.fields", "always");
        System.out.println("Conf Length : " + conf.size());
    }
}
