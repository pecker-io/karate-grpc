package demo;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.intuit.karate.cucumber.CucumberRunner;
import com.intuit.karate.cucumber.KarateStats;

import cucumber.api.CucumberOptions;
import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;

/**
 * DemoTestParallel
 *
 * @author thinkerou
 */
@CucumberOptions(tags = {"~@ignore"}) // IMPORTANT: don't use @RunWith(Karate.class)
public class DemoTestParallel {

    private static final int THREAD_COUNT = 5;

    @BeforeClass
    public static void beforeClass() {
        TestBase.beforeClass();
    }

    @AfterClass
    public static void afterClass() {
        TestBase.afterClass();
    }

    @Test
    public void testParallel() {
        String outputPath = "target/surefire-reports";
        KarateStats stats = CucumberRunner.parallel(getClass(), THREAD_COUNT, outputPath);
        generateReport(outputPath);
        assertTrue("There are scenario failure", stats.getFailCount() == 0);
    }

    private static void generateReport(String outputPath) {
        Collection<File> jsonFiles = FileUtils.listFiles(new File(outputPath), new String[] {"json"}, true);

        List<String> jsonPaths = new ArrayList<>(jsonFiles.size());
        jsonFiles.forEach(file -> jsonPaths.add(file.getAbsolutePath()));

        Configuration config = new Configuration(new File("target"), "gRPC Test by Karate");

        ReportBuilder reportBuilder = new ReportBuilder(jsonPaths, config);
        reportBuilder.generateReports();
    }

}
