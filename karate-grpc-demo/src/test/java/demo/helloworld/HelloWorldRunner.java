package demo.helloworld;

import demo.AbstractTestBase;

/**
 * HelloWorldRunner
 *
 * @author thinkerou
 */
public class HelloWorldRunner extends AbstractTestBase {
    @Override
    protected String getFeatures() {
        return "classpath:demo/helloworld/helloworld.feature";
    }
}
