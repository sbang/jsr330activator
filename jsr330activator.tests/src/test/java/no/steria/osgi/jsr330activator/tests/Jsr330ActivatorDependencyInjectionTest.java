package no.steria.osgi.jsr330activator.tests;

import static org.junit.Assert.*;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;

import javax.inject.Inject;

import no.steria.osgi.jsr330activator.Jsr330Activator;
import no.steria.osgi.jsr330activator.testbundle3.HelloService3a;
import no.steria.osgi.jsr330activator.testbundle3.HelloService3b;
import no.steria.osgi.jsr330activator.testbundle3.HelloService3c;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

/**
 * Integration test that tests that a bundle using a {@link Jsr330Activator}
 * loaded from the OSGi runtime can co-exist with a bundle that embeds the
 * {@link Jsr330Activator}.
 *
 * @author Steinar Bang
 *
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class Jsr330ActivatorDependencyInjectionTest extends Jsr330ActivatorIntegrationtestBase {

    @Inject
    private HelloService3a helloService3a;

    @Inject
    private HelloService3b helloService3b;

    @Inject
    private HelloService3c helloService3c;

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

    /**
     * Verifies that the implementation of the service {@link HelloService3a} gets
     * its injection and provides its service.
     */
    @Test
    public void testbundle3aServiceFoundAndActivated() {
    	assertEquals("I'm here to serve!", helloService3a.getMessage());
    }

    /**
     * Verifies that the implementation of the service {@link HelloService3b} gets
     * its injection and provides its service.
     */
    @Test
    public void testbundle3bServiceFoundAndActivated() {
    	assertEquals("Hello world2!", helloService3b.getMessage());
    }

    /**
     * Verifies that the implementation of the service {@link HelloService3c} gets
     * its injection and provides its service.
     */
    @Test
    public void testbundle3cServiceFoundAndActivated() {
    	assertEquals("I'm here to serve! Hello world2!", helloService3c.getCombinedMessage());
    }

}
