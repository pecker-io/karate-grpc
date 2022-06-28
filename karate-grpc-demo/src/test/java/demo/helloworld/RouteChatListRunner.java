package demo.helloworld;

import demo.TestBase;

public class RouteChatListRunner extends TestBase {
    @Override
    protected String getFeatures() {
        return "classpath:demo/helloworld/route-chat-list.feature";
    }
}
