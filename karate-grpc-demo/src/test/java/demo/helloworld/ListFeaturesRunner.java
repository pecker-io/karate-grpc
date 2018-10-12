package demo.helloworld;

import cucumber.api.CucumberOptions;
import demo.TestBase;

/**
 * ListFeaturesRunner
 *
 * @author thinkerou
 */
@CucumberOptions(features = "classpath:demo/helloworld/list-features.feature")
public class ListFeaturesRunner extends TestBase {
}
