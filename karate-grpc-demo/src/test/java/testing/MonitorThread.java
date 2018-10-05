package testing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MonitorThread
 *
 * @author thinkerou
 */
public class MonitorThread extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(MonitorThread.class);

    public MonitorThread(int port) {
        setDaemon(true);
        setName("stop-monitor-" + port);
    }

    @Override
    public void run() {
        logger.info("Starting thread: {}", getName());
    }

}
