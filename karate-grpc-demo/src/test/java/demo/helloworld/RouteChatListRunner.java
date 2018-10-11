package demo.helloworld;

import cucumber.api.CucumberOptions;
import demo.TestBase;

/**
 * RouteChatListRunner
 *
 * @author thinkerou
 */
@CucumberOptions(features = "classpath:demo/helloworld/route-chat-list.feature")
public class RouteChatListRunner extends TestBase {
}
