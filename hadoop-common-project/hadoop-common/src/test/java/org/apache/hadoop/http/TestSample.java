package org.apache.hadoop.http;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import java.util.Collection;
import java.util.Arrays;
import java.util.logging.Logger;

@RunWith(Parameterized.class)
public class TestSample {
  private final static Logger LOGGER = Logger.getLogger(TestSample.class.getName());

  private boolean testBoolean;
  private String testString;
  private int testNum;
  @Parameters(name="{index}: fib({0})={1}")
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][] { 
      { true, "abc", 10 }, 
      { true, "abc", 20 },
      { true, "xyz", 10 },
      { true, "xyz", 20 },
      { false, "abc", 10 },
      { false, "abc", 20 },
      { false, "xyz", 10 },
      { false, "xyz", 20 },
    });
  }

  public TestSample(boolean testBoolean, String testString, int testNum) {
    this.testBoolean = testBoolean;
    this.testString = testString;
    this.testNum = testNum;
  }
  
  @Test
  public void testScript() throws Exception {
    LOGGER.info("expected: " + testBoolean);
    LOGGER.info("input: " + testString);
    LOGGER.info("n: " + testNum);
  }
}