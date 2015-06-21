/**
 * This package contains mock implementations of the <a href="http://www.osgi.org/">OSGi framework</a>
 * similar to what's found in <a href="https://sling.apache.org/documentation/development/osgi-mock.html">Apache Sling OSGi mocks</a>
 * and in <a href="http://docs.spring.io/osgi/docs/current/reference/html/testing.html">Spring OSGi mocks</a>.
 *
 * <p>The implemented classes implement basic service registration, and retrieval,
 * service notification and callback registration.
 *
 * <p>These OSGi mocks were written for unit tests in the <a href="http://sbang.github.io/jsr330activator/">Jsr330Activator project</a>
 * because the <a href="https://sling.apache.org/documentation/development/osgi-mock.html">Apache Sling OSGi mocks</a>
 * were compiled against an older version of OSGi than the <a href="http://sbang.github.io/jsr330activator/">Jsr330Activator</a>
 * project was using, and because the <a href="http://docs.spring.io/osgi/docs/current/reference/html/testing.html">Spring OSGi mocks</a>
 * didn't return null references for non-existing services, and because the tests required
 * more logic than mockito mocks could provide.
 *
 * <p>These mocks can be used the <a href="http://sbang.github.io/jsr330activator/">Jsr330Activator</a>, they
 * only have dependencies to the OSGi core and the java runtime, and they can be easily
 * used in unit tests.
 *
 * <p>Add the following maven dependency:
 * <pre>
 *   &lt;project&gt;
 *    &lt;dependencies&gt;
 *     &lt;dependency&gt;
 *      &lt;groupId&gt;no.steria.osgi.jsr330activator&lt;/groupId&gt;
 *      &lt;artifactId&gt;jsr330activator.mocks&lt;/artifactId&gt;
 *      &lt;version&gt;1.0.1&lt;/version&gt;
 *      &lt;scope&gt;test&lt;/scope&gt;
 *     &lt;/dependency&gt;
 *    &lt;/dependencies&gt;
 *   &lt;/project&gt;
 * </pre>
 *
 * <p>Then in the test, create a {@link MockBundleContext} and give it as an argument where a BundleContext is expected, e.g. with a BundleActivator:
 * <code>
 * public class MyOsgiTests() {
 *     @Test
 *     public void testMyActivator() {
 *         {@code BundleContext = new MockBundleContext();
 *         MyActivator activator = new MyActivator();
 *
 *         // Check that the service can't be found before activator start
 *         ServiceReference<?> helloBeforeActivation = bundleContext.getServiceReference(HelloService.class.getCanonicalName());
 *         assertNull(helloBeforeActivation);
 *
 *         // Start the activator
 *         activator.start();
 *
 *         // Verify that the service can now be found
 *         ServiceReference<?> helloAfterActivation = bundleContext.getServiceReference(HelloService.class.getCanonicalName());
 *         assertNotNull(helloAfterActivation);
 *
 *         // Get the service from the reference and call it
 *         HelloService helloService = (HelloService) bundleContext.getService(helloAfterActivation);
 *         assertEquals("Hello world!", helloService.getMessage());
 *
 *         // Unregister the service and verify that there is no service of the type present
 *         activator.stop(bundleContext);
 *         ServiceReference<?> helloAfterDeactivation = bundleContext.getServiceReference(HelloService.class.getCanonicalName());
 *         assertNull(helloAfterDeactivation);}
 *     }
 * }
 * </code>
 *
 * @author Steinar Bang
 *
 */
package no.steria.osgi.mocks;
