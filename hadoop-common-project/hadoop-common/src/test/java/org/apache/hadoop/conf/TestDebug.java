package org.apache.hadoop.conf;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class TestDebug {

    @Test
    public void exampleTest() {
        Configuration conf = new Configuration();
        String name = conf.get("myname");
        String str = "hello";
        assertTrue(str.length() == -1);
    }

    @Test
    public void testECFuzzFail() throws Exception {
        Configuration conf = new Configuration();
        String ms = conf.get("fs.s3a.multipart.threshold");
        if (ms.equals("2147483647")) {
            throw new Exception("Fake Bug");
        }
    }

    @Test
    public void test(/*@From(ConfigurationGenerator.class) Configuration generatedConfig*/) throws Exception {
        Configuration conf = new Configuration();
        int count = 0;
        String name = conf.get("myname");
        String age = conf.get("myage");
        System.out.println("name: " + name + ", age: " + age);
        String str = conf.get("fs.s3a.select.output.csv.quote.fields");
        System.out.println(str);
        //System.out.println(str2);
        conf.set("fake2", "200");
        conf.set("fake3", "300");
        System.out.println("Fixed fake2 = " + conf.get("fake2"));
        System.out.println("Fixed fake3 = " + conf.get("fake3"));
        assertEquals("200", conf.get("fake2"));
        //System.out.println(str);
        if (str.equals("always")) {
            System.out.println("always");
            count ++;
            // This should fail if the fuzzer is not specificed with -DexpectedException=java.lang.AssertionError
            //assertEquals("200", conf.get("fake3"));
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
