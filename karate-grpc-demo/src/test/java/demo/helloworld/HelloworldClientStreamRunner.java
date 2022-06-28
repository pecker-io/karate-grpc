package demo.helloworld;

import demo.TestBase;

/**
 * HelloworldClientStreamRunner
 *
 * @author thinkerou
 */
public class HelloworldClientStreamRunner extends TestBase {
    @Override
    protected String getFeatures() {
        return "classpath:demo/helloworld/client-stream.feature";
    }
}
