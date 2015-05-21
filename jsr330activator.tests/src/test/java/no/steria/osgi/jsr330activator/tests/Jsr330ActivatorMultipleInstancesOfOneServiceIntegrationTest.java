package no.steria.osgi.jsr330activator.tests;

import static org.junit.Assert.*;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;

import java.util.Collection;
import java.util.UUID;

import javax.inject.Inject;

import no.steria.osgi.jsr330activator.testbundle7.CollectionInjectionCatcher;
import no.steria.osgi.jsr330activator.testbundle7.NamedServiceInjectionCatcher;
import no.steria.osgi.jsr330activator.testbundle8.StorageService;

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
     *
     * Get and use one of the services.
     */
    @Test
    public void testCollectionInjectionCatcherServiceFoundAndActivated() {
    	assertEquals(3, collectionInjectionCatcher.getNumberOfInjectedStorageServices());
    	Collection<String> serviceNames = collectionInjectionCatcher.getInjectedStorageServiceNames();
    	assertEquals(3, serviceNames.size());
    	UUID id = UUID.randomUUID();
    	String data = "Hi there!";
    	StorageService fileStorage = collectionInjectionCatcher.getStorageService("file");
    	boolean result = fileStorage.save(id, data);
    	assertTrue(result);
    }

    /**
     * Validate that a service depending on named injections of
     * multiple implementations of a service have been started.
     *
     * Get and use one of the services.
     */
    @Test
    public void testOptionalInjectionCatcherServiceFoundAndActivated() {
    	Collection<String> serviceNames = namedServiceInjectionCatcher.getInjectedStorageServiceNames();
    	assertEquals(3, serviceNames.size());
    	UUID id = UUID.randomUUID();
    	String data = "Hi there!";
    	StorageService fileStorage = namedServiceInjectionCatcher.getStorageService("file");
    	boolean result = fileStorage.save(id, data);
    	assertTrue(result);
    }

}
