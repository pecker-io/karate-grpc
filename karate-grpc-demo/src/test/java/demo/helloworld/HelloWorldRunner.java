package demo.helloworld;

import demo.TestBase;

/**
 * HelloWorldRunner
 *
 * @author thinkerou
 */
public class HelloWorldRunner extends TestBase {
    @Override
    protected String getFeatures() {
        return "classpath:demo/helloworld/helloworld.feature";
    }
}
