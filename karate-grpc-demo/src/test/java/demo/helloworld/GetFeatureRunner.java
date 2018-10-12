package demo.helloworld;

import cucumber.api.CucumberOptions;
import demo.TestBase;

/**
 * GetFeatureRunner
 *
 * @author thinkerou
 */
@CucumberOptions(features = "classpath:demo/helloworld/get-feature.feature")
public class GetFeatureRunner extends TestBase {
}
