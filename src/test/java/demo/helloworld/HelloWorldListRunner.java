package demo.helloworld;

import cucumber.api.CucumberOptions;
import demo.TestBase;

/**
 * HelloWorldListRunner
 *
 * @author thinkerou
 */
@CucumberOptions(features = "classpath:demo/helloworld/helloworld-list.feature")
public class HelloWorldListRunner extends TestBase {
}
