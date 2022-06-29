package demo.helloworld;

import demo.AbstractTestBase;

/**
 * HelloWorldListRunner
 *
 * @author thinkerou
 */
public class HelloWorldListRunner extends AbstractTestBase {
    @Override
    protected String getFeatures() {
        return "classpath:demo/helloworld/helloworld-list.feature";
    }
}
