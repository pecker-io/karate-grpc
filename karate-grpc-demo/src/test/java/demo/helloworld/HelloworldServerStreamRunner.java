package demo.helloworld;

import demo.TestBase;

/**
 * HelloworldServerStreamRunner
 *
 * @author thinkerou
 */
public class HelloworldServerStreamRunner extends TestBase {
    @Override
    protected String getFeatures() {
         return "classpath:demo/helloworld/server-stream.feature";
    }
}
