package org.apache.hadoop.conf;

import com.pholser.junit.quickcheck.From;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class TestDebug {

    @Test
    public void test(/*@From(ConfigurationGenerator.class) Configuration generatedConfig*/) throws Exception {
        Configuration conf = new Configuration();
        int count = 0;
        String name = conf.get("myname");
        String age = conf.get("myage");
        System.out.println("name: " + name + ", age: " + age);
        String str = conf.get("fs.s3a.select.output.csv.quote.fields");
        String str2 = conf.get("hadoop.http.staticuser.user");
        System.out.println(str2);
        conf.set("fake2", "200");
        conf.set("fake3", "300");
        assertEquals("200", conf.get("fake2"));
        //System.out.println(str);
        if (str.equals("always")) {
            System.out.println("always");
            count ++;
            // This should fail if the fuzzer is not specificed with -DexpectedException=java.lang.AssertionError
            assertEquals("200", conf.get("fake3"));
        } else if (str.equals("asneeded")) {
            System.out.println("asneeded");
	        conf.set("fake-config1","15");
            count --;
            throw new Exception("Fake Bug asneeded");
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
