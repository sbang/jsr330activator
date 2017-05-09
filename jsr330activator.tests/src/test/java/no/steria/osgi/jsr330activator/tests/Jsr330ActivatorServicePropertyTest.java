package no.steria.osgi.jsr330activator.tests;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;

import java.util.ArrayList;
import java.util.Arrays;

import javax.inject.Inject;

import no.steria.osgi.jsr330activator.ServiceProperties;
import no.steria.osgi.jsr330activator.ServiceProperty;
import no.steria.osgi.jsr330activator.testbundle1.HelloService;
import no.steria.osgi.jsr330activator.testbundle2.HelloService2;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * Integration test that tests that verifies that the properties set with
 * {@link ServiceProperty} annotations are available on injected services.
 *
 * @author Steinar Bang
 *
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class Jsr330ActivatorServicePropertyTest extends Jsr330ActivatorIntegrationtestBase {
    final private static String[] keysAlwaysPresentOnService = { "objectClass", "service.id" };

    @Inject
    private BundleContext bundleContext;

    @Configuration
    public Option[] config() {
        return options(
            systemProperty("logback.configurationFile").value("file:src/test/resources/logback.xml"),
            mavenBundle("org.slf4j", "slf4j-api", "1.7.2"),
            mavenBundle("ch.qos.logback", "logback-core", "1.0.4"),
            mavenBundle("ch.qos.logback", "logback-classic", "1.0.4"),
            mavenBundle("no.steria.osgi.jsr330activator", "jsr330activator.implementation", getMavenProjectVersion()),
            mavenBundle("no.steria.osgi.jsr330activator", "jsr330activator.testbundle1", getMavenProjectVersion()),
            mavenBundle("no.steria.osgi.jsr330activator", "jsr330activator.testbundle2", getMavenProjectVersion()),
            junitBundles());
    }

    /**
     * Verifies that a single {@link ServiceProperty} annotation on a service provider
     * is picked up and set as a property on the exposed OSGi service.
     */
    @Test
    public void testSingleServiceProperty() {
    	ServiceReference<HelloService> helloServiceReference = bundleContext.getServiceReference(HelloService.class);
    	String[] allKeys = helloServiceReference.getPropertyKeys();
    	assertEquals(3, allKeys.length);

        // Remove the keys that are always present on a service
    	ArrayList<String> customKeys = new ArrayList<String>(Arrays.asList(allKeys));
        customKeys.removeAll(Arrays.asList(keysAlwaysPresentOnService));

        // Verify that the remaining property has the expected key and value
        assertEquals(1, customKeys.size());
        String customKey = customKeys.get(0);
        String customValue = (String) helloServiceReference.getProperty(customKey);
        assertEquals("someprop", customKey);
        assertEquals("somevalue", customValue);
    }

    /**
     * Verifies that a multiple {@link ServiceProperty} annotations inside a
     * {@link ServiceProperties} on a service provider is picked up and set
     * as a property on the exposed OSGi service.
     */
    @Test
    public void testMultipleServiceProperties() {
    	ServiceReference<HelloService2> helloService2Reference = bundleContext.getServiceReference(HelloService2.class);
    	String[] allKeys = helloService2Reference.getPropertyKeys();
    	assertEquals(7, allKeys.length);

        // Remove the keys that are always present on a service
    	ArrayList<String> customKeys = new ArrayList<String>(Arrays.asList(allKeys));
        customKeys.removeAll(Arrays.asList(keysAlwaysPresentOnService));

        // Verify that the remaining property has the expected keys and values
        assertEquals(5, customKeys.size());
        assertThat(customKeys, hasItems("someprop", "otherprop", "lastprop", "multiple", "multiplewithstring"));
        assertEquals("somevalue", helloService2Reference.getProperty("someprop"));
        assertEquals("othervalue", helloService2Reference.getProperty("otherprop"));
        assertEquals("lastvalue", helloService2Reference.getProperty("lastprop"));

        // Verify that all values of a string array property value are present on the OSGi service property
        String[] multipleValues = (String[]) helloService2Reference.getProperty("multiple");
        assertEquals(2, multipleValues.length);
        assertEquals("val1", multipleValues[0]);
        assertEquals("val2", multipleValues[1]);

        // Verify that a property with both a string value and a string array value gets the string array value
        // as the property set on the OSGi service.
        String[] multipleValuesWithString = (String[]) helloService2Reference.getProperty("multiplewithstring");
        assertEquals(1, multipleValuesWithString.length);
        assertEquals("found1", multipleValuesWithString[0]);
    }

}
