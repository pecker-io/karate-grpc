package demo.helloworld;

import demo.AbstractTestBase;

/**
 * HelloWorldNewRunner
 *
 * @author thinkerou
 */
public class HelloWorldNewRunner extends AbstractTestBase {
    @Override
    protected String getFeatures() {
        return "classpath:demo/helloworld/helloworld-new.feature";
    }
}
