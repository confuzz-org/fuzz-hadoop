package org.apache.hadoop.conf;

import com.pholser.junit.quickcheck.From;
import edu.berkeley.cs.jqf.fuzz.Fuzz;
import edu.berkeley.cs.jqf.fuzz.JQF;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(JQF.class)
public class TestDebug2 {

    @Fuzz
    public void test(String s) throws Exception {
        System.out.println(s);
        String x = s + " haha";
        if (x.equals("yu haha")) {
            throw new IOException();
        }
    }

    @Test
    public void test2() {
        Configuration conf = new Configuration();
        conf.set("fs.s3a.select.output.csv.quote.fields", "always");
        System.out.println("Conf Length : " + conf.size());
    }
}
