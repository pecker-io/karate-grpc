package demo.helloworld;

import demo.TestBase;

/**
 * HelloworldBiStreamRunner
 *
 * @author thinkerou
 */
public class HelloworldBiStreamRunner extends TestBase {
    @Override
    protected String getFeatures() {
        return "classpath:demo/helloworld/bi-stream.feature";
    }
}
