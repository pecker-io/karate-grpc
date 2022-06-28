package demo.helloworld;

import demo.TestBase;

/**
 * RecordRouteRunner
 *
 * @author thinkerou
 */
public class RecordRouteRunner extends TestBase {
    @Override
    protected String getFeatures() {
        return "classpath:demo/helloworld/record-route.feature";
    }
}
