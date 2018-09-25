package demo;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import com.intuit.karate.junit4.Karate;

import testing.ServerStart;

/**
 * TestBase
 *
 * @author thinkerou
 */
@RunWith(Karate.class)
public class TestBase {

    private static ServerStart server;

    @BeforeClass
    public static void beforeClass() throws Exception {
        if (server == null) {
            server = new ServerStart();
        }
        server.startServer();
    }

    @AfterClass
    public static void afterClass() {

    }

}
