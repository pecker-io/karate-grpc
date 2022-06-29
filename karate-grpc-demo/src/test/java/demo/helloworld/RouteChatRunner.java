package demo.helloworld;

import demo.AbstractTestBase;

public class RouteChatRunner extends AbstractTestBase {
    @Override
    protected String getFeatures() {
        return "classpath:demo/helloworld/route-chat.feature";
    }
}
