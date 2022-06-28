package demo.helloworld;

import demo.TestBase;

/**
 * GetFeatureRunner
 *
 * @author thinkerou
 */
public class GetFeatureRunner extends TestBase {
    @Override
    protected String getFeatures() {
        return "classpath:demo/helloworld/get-feature.feature";
    }
}
