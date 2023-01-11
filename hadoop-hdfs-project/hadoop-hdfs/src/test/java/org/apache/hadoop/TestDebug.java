package org.apache.hadoop;
import org.junit.Test;
import org.apache.hadoop.conf.Configuration;

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
        //System.out.println(str);
        if (str.equals("always")) {
            System.out.println("always");
            count ++;
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
}
