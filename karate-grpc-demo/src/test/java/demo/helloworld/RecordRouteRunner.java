package demo.helloworld;

import demo.AbstractTestBase;

/**
 * RecordRouteRunner
 *
 * @author thinkerou
 */
public class RecordRouteRunner extends AbstractTestBase {
    @Override
    protected String getFeatures() {
        return "classpath:demo/helloworld/record-route.feature";
    }
}
