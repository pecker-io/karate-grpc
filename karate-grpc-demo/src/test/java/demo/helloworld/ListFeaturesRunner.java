package demo.helloworld;

import demo.TestBase;

/**
 * ListFeaturesRunner
 *
 * @author thinkerou
 */
public class ListFeaturesRunner extends TestBase {
    @Override
    protected String getFeatures() {
        return "classpath:demo/helloworld/list-features.feature";
    }
}
