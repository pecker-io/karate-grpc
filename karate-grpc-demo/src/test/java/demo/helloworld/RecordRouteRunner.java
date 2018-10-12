package demo.helloworld;

import cucumber.api.CucumberOptions;
import demo.TestBase;

/**
 * RecordRouteRunner
 *
 * @author thinkerou
 */
@CucumberOptions(features = "classpath:demo/helloworld/record-route.feature")
public class RecordRouteRunner extends TestBase {
}
