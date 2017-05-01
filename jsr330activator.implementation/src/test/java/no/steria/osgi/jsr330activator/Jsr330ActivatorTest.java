package no.steria.osgi.jsr330activator;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.steria.osgi.jsr330activator.implementation.ProviderAdapter;
import no.steria.osgi.jsr330activator.testbundle.AddInjectionsService;
import no.steria.osgi.jsr330activator.testbundle.HelloService;
import no.steria.osgi.jsr330activator.testbundle.HelloService2;
import no.steria.osgi.jsr330activator.testbundle.implementation.AddInjectionsServiceProvider;
import no.steria.osgi.jsr330activator.testbundle.implementation.HelloService2Provider;
import no.steria.osgi.jsr330activator.testbundle.implementation.HelloService2Provider2;
import no.steria.osgi.jsr330activator.testbundle.implementation.HelloServiceImplementation;
import no.steria.osgi.jsr330activator.testbundle.implementation.HelloServiceProvider;
import no.steria.osgi.jsr330activator.testbundle.implementation.HelloServiceProvider2;
import no.steria.osgi.mocks.MockBundle;
import no.steria.osgi.mocks.MockBundleContext;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
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
    	BundleWiring bundleWiring = mock(BundleWiring.class);
    	// Names of 3 classes found in the test project, name of 1 class not found (to test the try/catch)
    	List<String> classResources = Arrays.asList("no/steria/osgi/jsr330activator/testbundle/HelloService.class", "no/steria/osgi/jsr330activator/testbundle/implementation/HelloServiceImplementation.class", "no/steria/osgi/jsr330activator/testbundle/implementation/HelloServiceProvider.class", "no/steria/osgi/jsr330activator/testbundle/implementation/NotFoundClass.class");
    	when(bundleWiring.listResources(anyString(), anyString(), anyInt())).thenReturn(classResources);
    	when(bundleWiring.getClassLoader()).thenReturn(this.getClass().getClassLoader());
    	Bundle bundle = new MockBundle(bundleWiring);

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

    /***
     * Verify that the scanning code finds a provider that implements multiple
     * interfaces, where the Provider<> interface is first in the list.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testFindProvidersMultipleInterfacesWithProviderFirst() {
        List<Class<?>> classesInBundle = Arrays.asList(HelloService2.class, HelloServiceImplementation.class, HelloServiceProvider2.class);

        Jsr330Activator activator = new Jsr330Activator();
        Map<Type, Class<?>> providers = activator.findProviders(classesInBundle);
        assertEquals(1, providers.size());
        assertEquals(HelloServiceProvider2.class, providers.get(HelloService.class));
    }

    /***
     * Verify that the scanning code finds a provider that implements multiple
     * interfaces, where the Provider<> interface is not first in the interface list.
     */
    @Ignore
    @SuppressWarnings("unchecked")
    @Test
    public void testFindProvidersMultipleInterfacesWithProviderNotFirst() {
        List<Class<?>> classesInBundle = Arrays.asList(HelloService2.class, HelloServiceImplementation.class, HelloService2Provider2.class);

        Jsr330Activator activator = new Jsr330Activator();
        Map<Type, Class<?>> providers = activator.findProviders(classesInBundle);
        assertEquals(1, providers.size());
        assertEquals(HelloService2Provider2.class, providers.get(HelloService2.class));
    }

    @Test
    public void testCreateProviderAdapterList() throws InvalidSyntaxException {
    	Map<Type, Class<?>> providers = new HashMap<Type, Class<?>>();
    	providers.put(HelloService.class, HelloServiceProvider.class);
    	providers.put(HelloService2.class, HelloService2Provider.class);
    	providers.put(AddInjectionsService.class, AddInjectionsServiceProvider.class);

    	Jsr330Activator activator = new Jsr330Activator();
    	List<ProviderAdapter> providerAdapters = activator.createProviderAdapterList(providers);
    	assertEquals(3, providerAdapters.size());

    	for (ProviderAdapter providerAdapter : providerAdapters) {
            if (providerAdapter.getProvidedServiceType() == HelloService.class) {
                // The HelloService provider has no injections
                assertFalse(providerAdapter.hasInjections());
            }

            if (providerAdapter.getProvidedServiceType() == HelloService2.class) {
                // The HelloService2 provider has injections
                assertTrue(providerAdapter.hasInjections());
            }

            if (providerAdapter.getProvidedServiceType() == AddInjectionsService.class) {
                // The AddInjectionsService provider has injections
                assertTrue(providerAdapter.hasInjections());
            }
        }

    	// Start the provider adapters.
    	BundleContext bundleContext = mock(BundleContext.class);
    	when(bundleContext.getServiceReferences(anyString(), anyString())).thenReturn(new ServiceReference<?>[0]);
    	for (ProviderAdapter providerAdapter : providerAdapters) {
            providerAdapter.start(bundleContext);
        }
    }

    @Test
    public void testActivatorStartStop() throws Exception {
    	BundleWiring bundleWiring = mock(BundleWiring.class);
    	// Names of 3 classes found in the test project, name of 1 class not found (to test the try/catch)
    	List<String> classResources = Arrays.asList("no/steria/osgi/jsr330activator/testbundle/HelloService.class", "no/steria/osgi/jsr330activator/testbundle/implementation/HelloServiceImplementation.class", "no/steria/osgi/jsr330activator/testbundle/implementation/HelloServiceProvider.class", "no/steria/osgi/jsr330activator/testbundle/implementation/NotFoundClass.class");
    	when(bundleWiring.listResources(anyString(), anyString(), anyInt())).thenReturn(classResources);
     	when(bundleWiring.getClassLoader()).thenReturn(this.getClass().getClassLoader());
    	MockBundle bundle = new MockBundle(bundleWiring);
    	BundleContext bundleContext = new MockBundleContext(bundle);

    	// Verify that there is no HelloService before the registration
    	ServiceReference<?> helloBeforeActivation = bundleContext.getServiceReference(HelloService.class.getCanonicalName());
    	assertNull(helloBeforeActivation);

    	// Register the found services
    	Jsr330Activator activator = new Jsr330Activator();
    	activator.start(bundleContext);

    	// Verify that the service can now be found
    	ServiceReference<?> helloAfterActivation = bundleContext.getServiceReference(HelloService.class.getCanonicalName());
    	assertNotNull(helloAfterActivation);

    	// Get the service from the reference and call it
    	HelloService helloService = (HelloService) bundleContext.getService(helloAfterActivation);
    	assertEquals("Hello world!", helloService.getMessage());

    	// Unregister the service and verify that there is no service of that type present
    	activator.stop(bundleContext);
    	ServiceReference<?> helloAfterDeactivation = bundleContext.getServiceReference(HelloService.class.getCanonicalName());
    	assertNull(helloAfterDeactivation);
    }

}
