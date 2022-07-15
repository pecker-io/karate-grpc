package testing;

import java.io.IOException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.thinkerou.demo.helloworld.HelloWorldServerMain;

/**
 * ServerStart
 *
 * @author thinkerou
 */
public class ServerStart {

    private static final Logger logger = LoggerFactory.getLogger(ServerStart.class);

    private MonitorThread monitor;

    /**
     * @param args args
     * @throws IOException io exception
     * @throws InterruptedException interrupted exception
     */
    public void start(String[] args) throws IOException, InterruptedException {
        HelloWorldServerMain.main(args);
        logger.warn("Started server on port...");
        monitor = new MonitorThread(50051);
        monitor.start();
        monitor.join();
    }

    @Test
    public void startServer() throws Exception {
        start(new String[]{"test"});
    }

}
