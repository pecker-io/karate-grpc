package demo.helloworld;

import demo.AbstractTestBase;

/**
 * HelloworldBiStreamRunner
 *
 * @author thinkerou
 */
public class HelloworldBiStreamRunner extends AbstractTestBase {
    @Override
    protected String getFeatures() {
        return "classpath:demo/helloworld/bi-stream.feature";
    }
}
