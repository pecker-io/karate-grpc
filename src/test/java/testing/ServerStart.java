package testing;

import java.io.IOException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.thinkerou.demo.helloworld.HelloWorldServer;

/**
 * ServerStart
 *
 * @author thinkerou
 */
public class ServerStart {

    private static final Logger logger = LoggerFactory.getLogger(ServerStart.class);

    private MonitorThread monitor;

    public void start(String[] args) throws IOException, InterruptedException {
        HelloWorldServer.main(args);
        logger.info("Started server on port...");
        monitor = new MonitorThread(50021);
        monitor.start();
        monitor.join();
    }

    @Test
    public void startServer() throws Exception {
        start(new String[]{"test"});
    }

}
