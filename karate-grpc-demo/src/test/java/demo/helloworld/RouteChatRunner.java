package demo.helloworld;

import cucumber.api.CucumberOptions;
import demo.TestBase;

/**
 * RouteChatRunner
 *
 * @author thinkerou
 */
@CucumberOptions(features = "classpath:demo/helloworld/route-chat.feature")
public class RouteChatRunner extends TestBase {
}
