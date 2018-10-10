package demo.helloworld;

import cucumber.api.CucumberOptions;
import demo.TestBase;

/**
 * HelloworldBiStreamRunner
 *
 * @author thinkerou
 */
@CucumberOptions(features = "classpath:demo/helloworld/bi-stream.feature")
public class HelloworldBiStreamRunner extends TestBase {
}
