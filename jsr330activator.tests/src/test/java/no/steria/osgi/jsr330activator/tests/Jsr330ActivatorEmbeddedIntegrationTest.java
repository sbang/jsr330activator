package no.steria.osgi.jsr330activator.tests;

import static org.junit.Assert.*;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;

import javax.inject.Inject;

import no.steria.osgi.jsr330activator.Jsr330Activator;
import no.steria.osgi.jsr330activator.testbundle2.HelloService2;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

/**
 * Integration test that tests a bundle using an embedded {@link Jsr330Activator}
 * as its bundle activator.
 *
 * In this case there is no need to add the jsr330activator.implementation
 * bundle to the runtime.
 *
 * @author Steinar Bang
 *
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class Jsr330ActivatorEmbeddedIntegrationTest extends Jsr330ActivatorIntegrationtestBase {

    @Inject
    private HelloService2 helloService2;

    @Configuration
    public Option[] config() {
        return options(
                       systemProperty("logback.configurationFile").value("file:src/test/resources/logback.xml"),
                       mavenBundle("org.slf4j", "slf4j-api", "1.7.2"),
                       mavenBundle("ch.qos.logback", "logback-core", "1.0.4"),
                       mavenBundle("ch.qos.logback", "logback-classic", "1.0.4"),
                       mavenBundle("no.steria.osgi.jsr330activator", "jsr330activator.testbundle2", getMavenProjectVersion()),
                       junitBundles());
    }

    /**
     * Verifies that the activator in testbundle1 starts, finds a
     * service implementation, and registers it in the OSGi service
     * registry
     */
    @Test
    public void testbundle1ServiceFoundAndActivated() {
    	assertEquals("Hello world2!", helloService2.getMessage());
    }

}
