package demo.helloworld;

import demo.TestBase;

/**
 * HelloWorldListRunner
 *
 * @author thinkerou
 */
public class HelloWorldListRunner extends TestBase {
    @Override
    protected String getFeatures() {
        return "classpath:demo/helloworld/helloworld-list.feature";
    }
}
