package demo;

/**
 * DemoTest
 *
 * @author thinkerou
 */
public class DemoTest extends AbstractTestBase {
    @Override
    protected String getFeatures() {
        return "classpath:";
    }

    /**
     * This class will automatically pick up all *.feature files in src/test/java/demo
     * and even recurse sub-directories even though the class name ends with 'Test',
     * the maven 'pom.xml' has set 'DemoTestParallel' to be the default 'test suite'
     * for the whole project.
     */

}
