package demo.helloworld;

import demo.AbstractTestBase;

/**
 * ListFeaturesRunner
 *
 * @author thinkerou
 */
public class ListFeaturesRunner extends AbstractTestBase {
    @Override
    protected String getFeatures() {
        return "classpath:demo/helloworld/list-features.feature";
    }
}
