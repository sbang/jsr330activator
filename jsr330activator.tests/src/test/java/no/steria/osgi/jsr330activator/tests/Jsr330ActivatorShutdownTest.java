package no.steria.osgi.jsr330activator.tests;

import static org.junit.Assert.*;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

import no.steria.osgi.jsr330activator.ActivatorShutdown;
import no.steria.osgi.jsr330activator.Jsr330Activator;
import no.steria.osgi.jsr330activator.testbundle1.HelloService;
import no.steria.osgi.jsr330activator.testbundle3.HelloService3a;

/**
 * Integration test that tests shutting down a {@link Jsr330Activator}
 * will look for methods annotated with {@link ActivatorShutdown} in the
 * service providers discovered in the bundle, and call these methods
 * before shutting down.
 *
 * @author Steinar Bang
 *
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class Jsr330ActivatorShutdownTest extends Jsr330ActivatorIntegrationtestBase {

    @Inject
    private BundleContext bundleContext;

    @Inject
    private HelloService helloService;

    @Inject
    private HelloService3a helloService3a;

    @Configuration
    public Option[] config() {
        return options(
            systemProperty("logback.configurationFile").value("file:src/test/resources/logback.xml"),
            mavenBundle("org.slf4j", "slf4j-api", "1.7.2"),
            mavenBundle("ch.qos.logback", "logback-core", "1.0.4"),
            mavenBundle("ch.qos.logback", "logback-classic", "1.0.4"),
            mavenBundle("no.steria.osgi.jsr330activator", "jsr330activator.implementation", getMavenProjectVersion()),
            mavenBundle("no.steria.osgi.jsr330activator", "jsr330activator.testbundle1", getMavenProjectVersion()),
            mavenBundle("no.steria.osgi.jsr330activator", "jsr330activator.testbundle3", getMavenProjectVersion()),
            mavenBundle("no.steria.osgi.jsr330activator", "jsr330activator.testbundle2", getMavenProjectVersion()),
            junitBundles());
    }

    @Test
    public void shutdownAndCallback() throws BundleException {
        // Verify that the initial message of helloService is the message set by HelloService3a
        String initialRegisteredMessage = helloService.getMessage();
        assertEquals("I'm here to serve!", initialRegisteredMessage);

        // Prepare HelloService3a for shutdown
        String unregistrationMessage = "A kiss before dying!";
        helloService3a.setUnregistrationMessage(unregistrationMessage);

        // Shutting down the bundle containing helloService3a
        final String bundlePath = "mvn:no.steria.osgi.jsr330activator/jsr330activator.testbundle3/" + getMavenProjectVersion();
        Bundle bundle3 = bundleContext.getBundle(bundlePath);
        bundle3.stop();

        // Verifying that HelloService has received the unregistration message
        assertEquals(unregistrationMessage, helloService.getMessage());
    }

}
