package no.steria.osgi.jsr330activator.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;

import javax.inject.Inject;

import no.steria.osgi.jsr330activator.testbundle1.HelloService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.exam.util.PathUtils;
import org.osgi.framework.BundleContext;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class Jsr330ActivatorIntegrationTest {

    @Inject
    private BundleContext bc;

    @Inject
    private HelloService helloService;

    @Configuration
    public Option[] config() {
        return options(
                       systemProperty("logback.configurationFile").value("file:src/test/resources/logback.xml"),
                       mavenBundle("org.slf4j", "slf4j-api", "1.7.2"),
                       mavenBundle("ch.qos.logback", "logback-core", "1.0.4"),
                       mavenBundle("ch.qos.logback", "logback-classic", "1.0.4"),
                       mavenBundle("no.steria.osgi.jsr330activator", "jsr330activator.implementation", "0.0.1-SNAPSHOT"),
                       mavenBundle("no.steria.osgi.jsr330activator", "jsr330activator.testbundle1", "0.0.1-SNAPSHOT"),
                       junitBundles());
    }

    @Test
    public void shouldHaveBundleContext() {
        assertThat(bc, is(notNullValue()));
    }

    /**
     * Verifies that the activator in testbundle1 starts, finds a
     * service implementation, and registers it in the OSGi service
     * registry
     */
    @Test
    public void testbundle1ServiceFoundAndActivated() {
    	assertEquals("Hello world!", helloService.getMessage());
    }

}
