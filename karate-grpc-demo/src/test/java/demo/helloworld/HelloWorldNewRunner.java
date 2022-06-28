package demo.helloworld;

import demo.TestBase;

/**
 * HelloWorldNewRunner
 *
 * @author thinkerou
 */
public class HelloWorldNewRunner extends TestBase {
    @Override
    protected String getFeatures() {
        return "classpath:demo/helloworld/helloworld-new.feature";
    }
}
