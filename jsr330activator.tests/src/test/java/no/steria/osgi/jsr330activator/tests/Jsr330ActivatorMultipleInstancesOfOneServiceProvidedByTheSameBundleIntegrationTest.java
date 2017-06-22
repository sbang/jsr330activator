package no.steria.osgi.jsr330activator.tests;

import static org.junit.Assert.*;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;

import java.util.Collection;
import javax.inject.Inject;

import no.steria.osgi.jsr330activator.testbundle9.NamedServiceOptionalInjectionCatcher;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

/**
 * Integration test for bundles that contain multiple implementations of the same service.
 *
 * @author Steinar Bang
 *
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class Jsr330ActivatorMultipleInstancesOfOneServiceProvidedByTheSameBundleIntegrationTest extends Jsr330ActivatorIntegrationtestBase {

    @Inject
    private NamedServiceOptionalInjectionCatcher namedServiceOptionalInjectionCatcher;

    @Configuration
    public Option[] config() {
        return options(
            systemProperty("logback.configurationFile").value("file:src/test/resources/logback.xml"),
            mavenBundle("org.slf4j", "slf4j-api", "1.7.2"),
            mavenBundle("ch.qos.logback", "logback-core", "1.0.4"),
            mavenBundle("ch.qos.logback", "logback-classic", "1.0.4"),
            mavenBundle("no.steria.osgi.jsr330activator", "jsr330activator.testbundle8", getMavenProjectVersion()),
            mavenBundle("no.steria.osgi.jsr330activator", "jsr330activator.testbundle9", getMavenProjectVersion()),
            junitBundles());
    }

    /**
     * Verify that both instances of the storage service can be found.
     */
    @Test
    public void testOptionalInjectionCatcherServiceFoundAndActivated() {
        Collection<String> serviceNames = namedServiceOptionalInjectionCatcher.getInjectedStorageServiceNames();
        assertEquals(2, serviceNames.size());
    }

}
