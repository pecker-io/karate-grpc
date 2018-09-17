package demo.routeguide;

import cucumber.api.CucumberOptions;
import demo.TestBase;

/**
 * RouteGuideRunner
 *
 * @author thinkerou
 */
@CucumberOptions(features = "classpath:demo/routeguide/routeguide.featrue")
public class RouteGuideRunner extends TestBase {
}
