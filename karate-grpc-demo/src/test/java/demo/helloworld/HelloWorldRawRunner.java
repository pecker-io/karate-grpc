package demo.helloworld;

import demo.AbstractTestBase;

/**
 * HelloWorldRunner
 *
 * @author thinkerou
 */
public class HelloWorldRawRunner extends AbstractTestBase {
    @Override
    protected String getFeatures() {
        return "classpath:demo/helloworld/helloworld-raw.feature";
    }
}
