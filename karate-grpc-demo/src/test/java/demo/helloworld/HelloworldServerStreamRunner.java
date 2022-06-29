package demo.helloworld;

import demo.AbstractTestBase;

/**
 * HelloworldServerStreamRunner
 *
 * @author thinkerou
 */
public class HelloworldServerStreamRunner extends AbstractTestBase {
    @Override
    protected String getFeatures() {
         return "classpath:demo/helloworld/server-stream.feature";
    }
}
