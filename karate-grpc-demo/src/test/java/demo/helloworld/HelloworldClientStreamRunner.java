package demo.helloworld;

import cucumber.api.CucumberOptions;
import demo.TestBase;

/**
 * HelloworldClientStreamRunner
 *
 * @author thinkerou
 */
@CucumberOptions(features = "classpath:demo/helloworld/client-stream.feature")
public class HelloworldClientStreamRunner extends TestBase {
}
