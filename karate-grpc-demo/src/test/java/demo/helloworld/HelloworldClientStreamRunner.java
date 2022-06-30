package demo.helloworld;

import demo.AbstractTestBase;

/**
 * HelloworldClientStreamRunner
 *
 * @author thinkerou
 */
public class HelloworldClientStreamRunner extends AbstractTestBase {
    @Override
    protected String getFeatures() {
        return "classpath:demo/helloworld/client-stream.feature";
    }
}
