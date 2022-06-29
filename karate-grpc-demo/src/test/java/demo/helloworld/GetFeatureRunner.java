package demo.helloworld;

import demo.AbstractTestBase;

/**
 * GetFeatureRunner
 *
 * @author thinkerou
 */
public class GetFeatureRunner extends AbstractTestBase {
    @Override
    protected String getFeatures() {
        return "classpath:demo/helloworld/get-feature.feature";
    }
}
