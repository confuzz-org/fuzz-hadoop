package org.apache.hadoop.conf;

import com.pholser.junit.quickcheck.From;
import edu.berkeley.cs.jqf.fuzz.Fuzz;
import edu.berkeley.cs.jqf.fuzz.JQF;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

@RunWith(JQF.class)
public class TestDebug {

    @Fuzz
    public void test(@From(ConfigurationGenerator.class) Configuration generatedConfig) throws Exception {
        int count = 0;
        String str = generatedConfig.get("fs.s3a.select.output.csv.quote.fields");
        if (str.equals("always")) {
            count ++;
        } else if (str.equals("asneeded")) {
            generatedConfig.set("fake-config1","15");
            count --;
            throw new Exception("Fake Bug");
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
