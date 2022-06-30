package demo;

import com.github.thinkerou.karate.helper.Main;
import com.github.thinkerou.karate.utils.JedisMock;
import com.github.thinkerou.karate.utils.RedisHelper;
import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import testing.ServerStart;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 *
 * @author ericdriggs
 */
// important: do not use @RunWith(Karate.class) !
public abstract class AbstractTestBase {

    protected static final int THREAD_COUNT = 3;
    protected abstract String getFeatures();
    private static ServerStart server;

    @BeforeClass
    public static void beforeClass() throws Exception {
        if (server == null) {
            server = new ServerStart();
        }
        server.startServer();
        Main.putTestDescriptorSetsToRedis();
    }

    @Test
    public void testParallel() {
        Results results = Runner.path(getFeatures())
                .outputCucumberJson(true)
                .outputHtmlReport(true)
                .outputJunitXml(true)
                .parallel(THREAD_COUNT);
        generateReport(results.getReportDir());
        assertTrue(results.getErrorMessages(), results.getFailCount() == 0);
    }

    public void generateReport(String karateOutputPath) {
        Collection<File> jsonFiles = FileUtils.listFiles(new File(karateOutputPath), new String[] {"json"}, true);
        List<String> jsonPaths = new ArrayList<>(jsonFiles.size());
        jsonFiles.forEach(file -> jsonPaths.add(file.getAbsolutePath()));
        Configuration config = new Configuration(new File("target"), getFeatures());
        ReportBuilder reportBuilder = new ReportBuilder(jsonPaths, config);
        reportBuilder.generateReports();
    }

    @AfterClass
    public static void afterClass() throws IOException {
        JedisMock.getRedisServer().stop();
        RedisHelper.closeJedisPool();
    }

}