package demo.helloworld;

import cucumber.api.CucumberOptions;
import demo.TestBase;

/**
 * HelloworldServerStreamRunner
 *
 * @author thinkerou
 */
@CucumberOptions(features = "classpath:demo/helloworld/server-stream.feature")
public class HelloworldServerStreamRunner extends TestBase {
}
