package demo.helloworld;

import demo.AbstractTestBase;

public class RouteChatListRunner extends AbstractTestBase {
    @Override
    protected String getFeatures() {
        return "classpath:demo/helloworld/route-chat-list.feature";
    }
}
