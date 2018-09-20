package demo.helloworld;

import cucumber.api.CucumberOptions;
import demo.TestBase;

/**
 * HelloWorldNewRunner
 *
 * @author thinkerou
 */
@CucumberOptions(features = "classpath:demo/helloworld/helloworld-new.feature")
public class HelloWorldNewRunner extends TestBase {
}
