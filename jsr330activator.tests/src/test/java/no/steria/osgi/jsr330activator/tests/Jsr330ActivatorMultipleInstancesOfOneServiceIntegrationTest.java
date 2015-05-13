package no.steria.osgi.jsr330activator.tests;

import static org.junit.Assert.*;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;

import javax.inject.Inject;

import no.steria.osgi.jsr330activator.testbundle7.CollectionInjectionCatcher;
import no.steria.osgi.jsr330activator.testbundle7.NamedServiceInjectionCatcher;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

/**
 * Integration test for providers that receive multiple instances of
 * a single service.
 *
 * @author Steinar Bang
 *
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class Jsr330ActivatorMultipleInstancesOfOneServiceIntegrationTest extends Jsr330ActivatorIntegrationtestBase {

    @Inject
    private CollectionInjectionCatcher collectionInjectionCatcher;

    @Inject
    private NamedServiceInjectionCatcher namedServiceInjectionCatcher;

    @Configuration
    public Option[] config() {
        return options(
                       systemProperty("logback.configurationFile").value("file:src/test/resources/logback.xml"),
                       mavenBundle("org.slf4j", "slf4j-api", "1.7.2"),
                       mavenBundle("ch.qos.logback", "logback-core", "1.0.4"),
                       mavenBundle("ch.qos.logback", "logback-classic", "1.0.4"),
                       mavenBundle("no.steria.osgi.jsr330activator", "jsr330activator.testbundle8", getMavenProjectVersion()),
                       mavenBundle("no.steria.osgi.jsr330activator", "jsr330activator.testbundle4", getMavenProjectVersion()),
                       mavenBundle("no.steria.osgi.jsr330activator", "jsr330activator.testbundle5", getMavenProjectVersion()),
                       mavenBundle("no.steria.osgi.jsr330activator", "jsr330activator.testbundle6", getMavenProjectVersion()),
                       mavenBundle("no.steria.osgi.jsr330activator", "jsr330activator.testbundle7", getMavenProjectVersion()),
                       junitBundles());
    }

    /**
     * Validate that a service depending on a collection injection has
     * been started.  Verify that the expected number of services has
     * been injected.
     */
    @Test
    public void testCollectionInjectionCatcherServiceFoundAndActivated() {
    	assertEquals(3, collectionInjectionCatcher.getNumberOfInjectedStorageServices());
    }

    /**
     * Validate that a service depending on named injections of
     * multiple implementations of a service have been started.
     */
    @Test
    public void testOptionalInjectionCatcherServiceFoundAndActivated() {
        assertNull(namedServiceInjectionCatcher.getMessage());
    }

}
