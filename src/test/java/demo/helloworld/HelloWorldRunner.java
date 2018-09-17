package demo.helloworld;

import cucumber.api.CucumberOptions;
import demo.TestBase;

/**
 * HelloWorldRunner
 *
 * @author thinkerou
 */
@CucumberOptions(features = "classpath:demo/helloworld/helloworld.feature")
public class HelloWorldRunner extends TestBase {
}
