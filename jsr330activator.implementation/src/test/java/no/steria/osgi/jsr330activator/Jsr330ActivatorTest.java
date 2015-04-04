package no.steria.osgi.jsr330activator;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.steria.osgi.jsr330activator.testbundle.HelloService;
import no.steria.osgi.jsr330activator.testbundle.implementation.HelloServiceImplementation;
import no.steria.osgi.jsr330activator.testbundle.implementation.HelloServiceProvider;

import org.apache.sling.testing.mock.osgi.MockOsgi;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.wiring.BundleWiring;

public class Jsr330ActivatorTest {

    @Before
    public void setUp() throws Exception {
    }

    /**
     * Unit test for {@link Jsr330Activator#scanBundleForClasses(Bundle)}.
     */
    @Test
    public void testScanBundleForClasses() {
    	Bundle bundle = mock(Bundle.class);
    	BundleWiring bundleWiring = mock(BundleWiring.class);
    	// Names of 3 classes found in the test project, name of 1 class not found (to test the try/catch)
    	List<String> classnames = Arrays.asList("no.steria.osgi.jsr330activator.testbundle.HelloService", "no.steria.osgi.jsr330activator.testbundle.implementation.HelloServiceImplementation", "no.steria.osgi.jsr330activator.testbundle.implementation.HelloServiceProvider", "no.steria.osgi.jsr330activator.testbundle.implementation.NotFoundClass");
    	when(bundleWiring.listResources(anyString(), anyString(), eq(BundleWiring.LISTRESOURCES_LOCAL))).thenReturn(classnames);
    	when(bundleWiring.getClassLoader()).thenReturn(this.getClass().getClassLoader());
    	when(bundle.adapt(eq(BundleWiring.class))).thenReturn(bundleWiring);

    	Jsr330Activator activator = new Jsr330Activator();
        List<Class<?>> bundleClasses = activator.scanBundleForClasses(bundle);
        assertEquals(3, bundleClasses.size());
    }

    /**
     * Corner case unit test for {@link Jsr330Activator#scanBundleForClasses(Bundle)}.
     * Test the case where {@link Bundle#adapt(Class)} returns null for a {@link BundleWiring} class.
     */
    @Test
    public void testScanBundleForClassesNullBundleWiringFromAdapt() {
    	Bundle bundle = mock(Bundle.class);

    	Jsr330Activator activator = new Jsr330Activator();
        List<Class<?>> bundleClasses = activator.scanBundleForClasses(bundle);
        assertEquals(0, bundleClasses.size());
    }

    /**
     * Corner case unit test for {@link Jsr330Activator#scanBundleForClasses(Bundle)}.
     * Test the case where {@link Bundle#adapt(Class)} for a {@link BundleWiring} class throws a {@link SecurityException}.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testScanBundleForClassesBundleWiringAdaptThrowsSecurityException() {
    	Bundle bundle = mock(Bundle.class);
    	when(bundle.adapt(eq(BundleWiring.class))).thenThrow(SecurityException.class);

    	Jsr330Activator activator = new Jsr330Activator();
        List<Class<?>> bundleClasses = activator.scanBundleForClasses(bundle);
        assertEquals(0, bundleClasses.size());
    }

    /**
     * Corner case unit test for {@link Jsr330Activator#scanBundleForClasses(Bundle)}.
     * Test the case where {@link BundleWiring#getClassLoader()} returns null.
     */
    @Test
    public void testScanBundleForClassesNullClassloaderFromBundleWiring() {
    	Bundle bundle = mock(Bundle.class);
    	BundleWiring bundleWiring = mock(BundleWiring.class);
    	List<String> classnames = Arrays.asList("no.steria.osgi.jsr330activator.testbundle.HelloService", "no.steria.osgi.jsr330activator.testbundle.implementation.HelloServiceImplementation", "no.steria.osgi.jsr330activator.testbundle.implementation.HelloServiceProvider", "no.steria.osgi.jsr330activator.testbundle.implementation.NotFoundClass");
    	when(bundleWiring.listResources(anyString(), anyString(), eq(BundleWiring.LISTRESOURCES_LOCAL))).thenReturn(classnames);
    	when(bundle.adapt(eq(BundleWiring.class))).thenReturn(bundleWiring);

    	Jsr330Activator activator = new Jsr330Activator();
        List<Class<?>> bundleClasses = activator.scanBundleForClasses(bundle);
        assertEquals(0, bundleClasses.size());
    }

    /**
     * Corner case unit test for {@link Jsr330Activator#scanBundleForClasses(Bundle)}.
     * Test the case where {@link BundleWiring#getClassLoader()} throws a {@link SecurityException}.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testScanBundleForClassesBundleWiringGetClassLoaderThrowsSecurityException() {
    	Bundle bundle = mock(Bundle.class);
    	BundleWiring bundleWiring = mock(BundleWiring.class);
    	List<String> classnames = Arrays.asList("no.steria.osgi.jsr330activator.testbundle.HelloService", "no.steria.osgi.jsr330activator.testbundle.implementation.HelloServiceImplementation", "no.steria.osgi.jsr330activator.testbundle.implementation.HelloServiceProvider", "no.steria.osgi.jsr330activator.testbundle.implementation.NotFoundClass");
    	when(bundleWiring.listResources(anyString(), anyString(), eq(BundleWiring.LISTRESOURCES_LOCAL))).thenReturn(classnames);
    	when(bundleWiring.getClassLoader()).thenThrow(SecurityException.class);
    	when(bundle.adapt(eq(BundleWiring.class))).thenReturn(bundleWiring);

    	Jsr330Activator activator = new Jsr330Activator();
        List<Class<?>> bundleClasses = activator.scanBundleForClasses(bundle);
        assertEquals(0, bundleClasses.size());
    }

    /**
     * Corner case unit test for {@link Jsr330Activator#scanBundleForClasses(Bundle)}.
     * Test the case where {@link BundleWiring#listResources(String, String, int)} returns a null (for a BundleWiring not in use).
     */
    @Test
    public void testScanBundleForClassesListResourcesReturnsNull() {
    	Bundle bundle = mock(Bundle.class);
    	BundleWiring bundleWiring = mock(BundleWiring.class);
    	when(bundleWiring.listResources(anyString(), anyString(), eq(BundleWiring.LISTRESOURCES_LOCAL))).thenReturn(null);
    	when(bundle.adapt(eq(BundleWiring.class))).thenReturn(bundleWiring);

    	Jsr330Activator activator = new Jsr330Activator();
        List<Class<?>> bundleClasses = activator.scanBundleForClasses(bundle);
        assertEquals(0, bundleClasses.size());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFindProviders() {
    	List<Class<?>> classesInBundle = Arrays.asList(HelloService.class, HelloServiceImplementation.class, HelloServiceProvider.class);

    	Jsr330Activator activator = new Jsr330Activator();
    	Map<Type, Class<?>> providers = activator.findProviders(classesInBundle);
    	assertEquals(1, providers.size());
    	assertEquals(HelloServiceProvider.class, providers.get(HelloService.class));
    }

    @Test
    public void testRegisterServices() {
    	BundleContext bundleContext = MockOsgi.newBundleContext();

    	// Verify that there is no HelloService before the registration
    	ServiceReference<?> helloBeforeActivation = bundleContext.getServiceReference(HelloService.class.getCanonicalName());
    	assertNull(helloBeforeActivation);

    	// Register the found services
    	Map<Type, Class<?>> serviceImplementations = new HashMap<Type, Class<?>>();
    	serviceImplementations.put(HelloService.class, HelloServiceProvider.class);
    	Jsr330Activator activator = new Jsr330Activator();
    	activator.registerServices(bundleContext, serviceImplementations);

    	// Verify that the service can now be found
    	ServiceReference<?> helloAfterActivation = bundleContext.getServiceReference(HelloService.class.getCanonicalName());
    	assertNotNull(helloAfterActivation);
    }

}
