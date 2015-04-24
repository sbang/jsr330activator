package no.steria.osgi.jsr330activator.implementation;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.List;

import no.steria.osgi.jsr330activator.testbundle.AddInjectionsService;
import no.steria.osgi.jsr330activator.testbundle.HelloService;
import no.steria.osgi.jsr330activator.testbundle.HelloService2;
import no.steria.osgi.jsr330activator.testbundle.implementation.AddInjectionsServiceProvider;
import no.steria.osgi.jsr330activator.testbundle.implementation.HelloService2Provider;
import no.steria.osgi.jsr330activator.testbundle.implementation.HelloServiceProvider;
import no.steria.osgi.mocks.MockBundleContext;

import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

/**
 * Unit tests for {@link ProviderAdapter}.
 *
 * @author Steinar Bang
 *
 */
public class ProviderAdapterTest {

    @Before
    public void setUp() throws Exception {
    }

    /**
     * Unit test for {@link ProviderAdapter#findInjections(Class)}.
     *
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws SecurityException
     * @throws NoSuchFieldException
     */
    @Test
    public void testFindInjections() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        // A provider with no injections should find none
        HelloServiceProvider helloServiceProvider = new HelloServiceProvider();
        List<Injection> injections = ProviderAdapter.findInjections(helloServiceProvider);
        assertEquals(0, injections.size());

        // A provider with a single injection should find one injection
        HelloService2Provider helloServiceProvider2 = new HelloService2Provider();
        List<Injection> injections2 = ProviderAdapter.findInjections(helloServiceProvider2);
        assertEquals(1, injections2.size());
        assertFalse(injections2.get(0).isInjected());
        assertNull(getPrivateField(helloServiceProvider2, "helloService"));
        injections2.get(0).doInject(helloServiceProvider.get());
        assertTrue(injections2.get(0).isInjected());
        assertNotNull(getPrivateField(helloServiceProvider2, "helloService"));

        // A provider with two injections should find two injections.
        AddInjectionsServiceProvider addInjectionsServiceProvider = new AddInjectionsServiceProvider();
        List<Injection> injections3 = ProviderAdapter.findInjections(addInjectionsServiceProvider);
        assertEquals(2, injections3.size());
        assertFalse(injections3.get(0).isInjected());
        assertFalse(injections3.get(1).isInjected());
    }

    /**
     * Unit test of the {@link ProviderAdapter#start(org.osgi.framework.BundleContext)} method.
     */
    @Test
    public void testStart() {
    	MockBundleContext bundleContext = new MockBundleContext();

    	// Create an adapter for a service that has no injections
    	ProviderAdapter providerAdapter = new ProviderAdapter(HelloService.class, HelloServiceProvider.class);
    	providerAdapter.start(bundleContext);
    	ServiceReference<?> helloServiceReference = bundleContext.getServiceReference(HelloService.class.getCanonicalName());
    	HelloService helloService = (HelloService) bundleContext.getService(helloServiceReference);
    	assertTrue(helloService instanceof HelloService);

    	// Start an adapter for a service that needs the first service as an injection
    	ProviderAdapter providerAdapter2 = new ProviderAdapter(HelloService2.class, HelloService2Provider.class);
    	providerAdapter2.start(bundleContext);
    	ServiceRegistration<?> helloService2Registration = providerAdapter2.getServiceRegistration();
    	assertNotNull(helloService2Registration);
    	ServiceReference<?> helloService2Reference = helloService2Registration.getReference();
    	HelloService2 helloService2 = (HelloService2) bundleContext.getService(helloService2Reference);
    	assertTrue(helloService2 instanceof HelloService2);
    }

    /**
     * Unit test of the {@link ProviderAdapter#start(org.osgi.framework.BundleContext)} method.
     */
    @Test
    public void testStop() {
        MockBundleContext bundleContext = new MockBundleContext();

        // Start services
        ProviderAdapter providerAdapter = new ProviderAdapter(HelloService.class, HelloServiceProvider.class);
        providerAdapter.start(bundleContext);
        ProviderAdapter providerAdapter2 = new ProviderAdapter(HelloService2.class, HelloService2Provider.class);
        providerAdapter2.start(bundleContext);

        // Verify that the second service is registered with OSGi
        ServiceReference<?> helloService2Registration = bundleContext.getServiceReference(HelloService2.class.getCanonicalName());
        assertNotNull(helloService2Registration);

        // Stop the service
        providerAdapter2.stop(bundleContext);

        // Verify that the second service no longer is registered with OSGi
        ServiceReference<?> helloService2RegistrationAfterStop = bundleContext.getServiceReference(HelloService2.class.getCanonicalName());
        assertNull(helloService2RegistrationAfterStop);
    }

    /**
     * Unit test for {@link ProviderAdapter#setupInjectionListeners(org.osgi.framework.BundleContext)}.
     */
    @Test
    public void testSetupInjectionListenersWaitForInjections() {
    	MockBundleContext bundleContext = new MockBundleContext();

    	ProviderAdapter providerAdapter = new ProviderAdapter(HelloService2.class, HelloService2Provider.class);

    	// Verify that the service isn't available before the listeners are set up
    	assertNull(providerAdapter.getServiceRegistration());

    	// Register the listeners
    	providerAdapter.setupInjectionListeners(bundleContext);

    	// Service should still not be available because the injections are not available yet
    	assertNull(providerAdapter.getServiceRegistration());

    	// Register the service that is to be injected
    	ProviderAdapter injectedDependencyProviderAdapter = new ProviderAdapter(HelloService.class, HelloServiceProvider.class);
    	injectedDependencyProviderAdapter.start(bundleContext);

    	// The injection dependency is satisfied, the service should now be available
    	assertNotNull(providerAdapter.getServiceRegistration());
    }

    /**
     * Unit test for {@link ProviderAdapter#setupInjectionListeners(org.osgi.framework.BundleContext)}.
     */
    @Test
    public void testSetupInjectionListenersInjectionAlreadyPresent() {
        MockBundleContext bundleContext = new MockBundleContext();

        // Register the service that is to be injected
        ProviderAdapter injectedDependencyProviderAdapter = new ProviderAdapter(HelloService.class, HelloServiceProvider.class);
        injectedDependencyProviderAdapter.start(bundleContext);

        // Adapter for the service provider requiring the injected service
        ProviderAdapter providerAdapter = new ProviderAdapter(HelloService2.class, HelloService2Provider.class);

        // Verify that the service isn't available before the listeners are set up
        assertNull(providerAdapter.getServiceRegistration());

        // Register the listeners
        providerAdapter.setupInjectionListeners(bundleContext);

        // The injection dependency is satisfied, the service should now be available
        assertNotNull(providerAdapter.getServiceRegistration());
    }

    /**
     * Unit test for {@link ProviderAdapter#setupInjectionListeners(org.osgi.framework.BundleContext)}.
     * Verify that all injections must be in place before the service is made available.
     */
    @Test
    public void testSetupInjectionListenersWaitForAllInjections() {
        MockBundleContext bundleContext = new MockBundleContext();

        ProviderAdapter providerAdapter = new ProviderAdapter(AddInjectionsService.class, AddInjectionsServiceProvider.class);

        // Verify that the service isn't available before the listeners are set up
        assertNull(bundleContext.getServiceReference(AddInjectionsService.class.getCanonicalName()));

        // Register the listeners
        providerAdapter.setupInjectionListeners(bundleContext);

        // Service should still not be available because the injections are not available yet
        assertNull(bundleContext.getServiceReference(AddInjectionsService.class.getCanonicalName()));

        // Register the first service that is to be injected
        Integer integerService = 42;
        bundleContext.registerService(Integer.class.getCanonicalName(), integerService, null);

        // All injection dependences aren't satisfied yet, the service is still unavailable
        assertNull(bundleContext.getServiceReference(AddInjectionsService.class.getCanonicalName()));

        // Register the second service that is to be injected
        String stringService = "This is a service";
        bundleContext.registerService(String.class.getCanonicalName(), stringService, null);

        // All injection dependecies have been injected and the service is now available.
        assertNotNull(bundleContext.getServiceReference(AddInjectionsService.class.getCanonicalName()));
    }

    private Object getPrivateField(Object object, String fieldName) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Class<? extends Object> clazz = object.getClass();
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(object);
    }

    /**
     * Unit test for {@link ProviderAdapter#setupInjectionListeners(org.osgi.framework.BundleContext)}.
     * Verify that removing an injection will unregister the service.
     */
    @Test
    public void testSetupInjectionListenersUnregisteredWhenInjectionsGoAway() {
        MockBundleContext bundleContext = new MockBundleContext();

        // Register the services that are to be injected
        Integer integerService = 42;
        bundleContext.registerService(Integer.class.getCanonicalName(), integerService, null);
        String stringService = "This is a service";
        ServiceRegistration<?> stringServiceRegistration = bundleContext.registerService(String.class.getCanonicalName(), stringService, null);

        ProviderAdapter providerAdapter = new ProviderAdapter(AddInjectionsService.class, AddInjectionsServiceProvider.class);

        // Verify that the service isn't available before the listeners are set up
        assertNull(bundleContext.getServiceReference(AddInjectionsService.class.getCanonicalName()));

        // Register the listeners
        providerAdapter.setupInjectionListeners(bundleContext);

        // Service should still not be available because the injections are not available yet
        assertNotNull(bundleContext.getServiceReference(AddInjectionsService.class.getCanonicalName()));

        // Unregister an injected service
        stringServiceRegistration.unregister();

        // Verify that the service is now unavailable.
        assertNull(bundleContext.getServiceReference(AddInjectionsService.class.getCanonicalName()));
    }

}