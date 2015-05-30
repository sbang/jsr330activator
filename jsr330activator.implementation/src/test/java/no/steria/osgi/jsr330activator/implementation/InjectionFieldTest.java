package no.steria.osgi.jsr330activator.implementation;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.inject.Named;

import no.steria.osgi.jsr330activator.Optional;
import no.steria.osgi.jsr330activator.testbundle.HelloService;
import no.steria.osgi.jsr330activator.testbundle.StorageService;
import no.steria.osgi.jsr330activator.testbundle.implementation.CollectionCatcherServiceProvider;
import no.steria.osgi.jsr330activator.testbundle.implementation.CollectionCatcherServiceProviderWithCollectionInject;
import no.steria.osgi.jsr330activator.testbundle.implementation.CollectionCatcherServiceProviderWithLinkedList;
import no.steria.osgi.jsr330activator.testbundle.implementation.CollectionCatcherServiceProviderWithNonGenericList;
import no.steria.osgi.jsr330activator.testbundle.implementation.CollectionCatcherServiceProviderWithSetInject;
import no.steria.osgi.jsr330activator.testbundle.implementation.CollectionCatcherServiceProviderWithUninitializedSetInject;
import no.steria.osgi.jsr330activator.testbundle.implementation.CollectionCatcherServiceProviderWithUninitializedTreeSetInject;
import no.steria.osgi.jsr330activator.testbundle.implementation.DummyStorageServiceProvider;
import no.steria.osgi.jsr330activator.testbundle.implementation.HelloService2Provider;
import no.steria.osgi.jsr330activator.testbundle.implementation.HelloServiceProvider;
import no.steria.osgi.jsr330activator.testbundle.implementation.MemoryStorageServiceProvider;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link InjectionField}.
 *
 * @author Steinar Bang
 *
 */
public class InjectionFieldTest {

    private HelloServiceProvider helloServiceProvider;
    private HelloService2Provider helloService2Provider;

    // Fields used for "isOptional" tests

    @Inject
    List<HelloService> nonOptionalCollection;

    @Inject
    @Optional
    List<HelloService> optionalCollection;

    @Inject
    @Named("named")
    HelloService nonOptionalNamed;

    @Inject
    @Named("namedoptional")
    @Optional
    HelloService optionalNamed;

    @Inject
    @Optional
    HelloService optionalUnnamed;

    @Before
    public void setUp() throws Exception {
        helloServiceProvider = new HelloServiceProvider();
        helloService2Provider = new HelloService2Provider();
    }

    /**
     * Unit tests for {@link InjectionField#isInjected()}.
     *
     * @throws NoSuchFieldException
     * @throws SecurityException
     */
    @Test
    public void testIsInjected() throws NoSuchFieldException, SecurityException {
        Field injectionPoint = helloService2Provider.getClass().getDeclaredField("helloService");

        Injection injectionField = new InjectionField(helloService2Provider, injectionPoint);
        assertFalse(injectionField.isInjected());

        // Inject a real HelloService
        HelloService helloService = helloServiceProvider.get();
        injectionField.doInject(helloService);
        assertTrue(injectionField.isInjected());

        // Retract the service and check the injected state
        injectionField.doRetract(helloService);
        assertFalse(injectionField.isInjected());
    }

    /**
     * Corner case unit test for {@link InjectionField#doInject(Object)}.
     *
     * Test that trying to inject a service of the wrong type results
     * in no injection.
     *
     * @throws SecurityException
     * @throws NoSuchFieldException
     */
    @Test
    public void testDoInjectWrongType() throws NoSuchFieldException, SecurityException  {
        Field injectionPoint = helloService2Provider.getClass().getDeclaredField("helloService");

        // Create InjectionField and verify uninjected state.
        Injection injectionField = new InjectionField(helloService2Provider, injectionPoint);
        assertFalse(injectionField.isInjected());

        // Try injection something other than the expected type and verify
        // that the InjectionField stays uninjected.
        String notAHelloService = "This is not HelloService!";
        injectionField.doInject(notAHelloService);
        assertFalse(injectionField.isInjected());
    }

    /**
     * Corner case unit test for {@link InjectionField#doInject(Object)}.
     *
     * Test that using a Field belonging to a different type results
     * in no injection.
     *
     * @throws SecurityException
     * @throws NoSuchFieldException
     */
    @Test
    public void testDoInjectFieldBelongsToDifferentType() throws NoSuchFieldException, SecurityException  {
        Field injectionPointNotAFieldOnProvider = this.getClass().getDeclaredField("helloServiceProvider");

        // Create InjectionField and verify uninjected state.
        Injection injectionField = new InjectionField(helloService2Provider, injectionPointNotAFieldOnProvider);
        assertFalse(injectionField.isInjected());

        // Try injection something other than the expected type and verify
        // that the InjectionField stays uninjected.
        String notAHelloService = "This is not HelloService!";
        injectionField.doInject(notAHelloService);
        assertFalse(injectionField.isInjected());
    }

    /**
     * Unit test for {@link InjectionField#doInject(Object)}.
     *
     * @throws SecurityException
     * @throws NoSuchFieldException
     */
    @Test
    public void testDoRetract() throws NoSuchFieldException, SecurityException {
        Field injectionPoint = helloService2Provider.getClass().getDeclaredField("helloService");
        Injection injectionField = new InjectionField(helloService2Provider, injectionPoint);

        // Inject a real HelloService
        HelloService helloService = helloServiceProvider.get();
        injectionField.doInject(helloService);
        assertTrue(injectionField.isInjected());

        // Retract the service and verify that it's gone
        injectionField.doRetract(helloService);
        assertFalse(injectionField.isInjected());
    }

    /**
     * Unit test for injecting into a field that is a
     * {@link Collection} (i.e. the interface containing
     * the basic operations necessary for the injection:
     * {@link Collection#add(Object)}, {@link Collection#contains(Object)},
     * and {@link Collection#remove(Object)}
     * @throws SecurityException
     * @throws NoSuchFieldException
     */
    @Test
    public void testCollectionInjection() throws NoSuchFieldException, SecurityException {
    	CollectionCatcherServiceProviderWithCollectionInject collectionCatcher = new CollectionCatcherServiceProviderWithCollectionInject();
    	Field injectionPoint = collectionCatcher.getClass().getDeclaredField("storageServices");
    	Injection injectionField = new InjectionField(collectionCatcher, injectionPoint);

    	// Verify that the injection type is the element type of the collection
    	assertEquals(StorageService.class, injectionField.getInjectedServiceType());

    	// Verify that the field isn't initially injected
    	assertFalse(injectionField.isInjected());

    	// Inject something not a storage service, this should be a no-op wrt. injection (ie. field still not injected)
    	injectionField.doInject(helloServiceProvider.get());
    	assertFalse(injectionField.isInjected());

    	// Inject a storage service
    	MemoryStorageServiceProvider memoryStorageServiceProvider = new MemoryStorageServiceProvider();
    	injectionField.doInject(memoryStorageServiceProvider.get());
    	assertTrue(injectionField.isInjected());

    	// Inject another storage service
    	DummyStorageServiceProvider dummyStorageProvider = new DummyStorageServiceProvider();
    	injectionField.doInject(dummyStorageProvider.get());
    	assertTrue(injectionField.isInjected());

    	// Remove one of the storage services, field is still injected (removing an already removed service is a no-op)
    	injectionField.doRetract(dummyStorageProvider.get());
    	assertTrue(injectionField.isInjected());

    	// Remove the same storage service again, field should still be injected
    	injectionField.doRetract(dummyStorageProvider.get());
    	assertTrue(injectionField.isInjected());

    	// Remove the other storage service, field should no longer be injected
    	injectionField.doRetract(memoryStorageServiceProvider.get());
    	assertFalse(injectionField.isInjected());
    }

    /**
     * Unit test for injecting into a field that is a
     * collection value that is a {@link List}. This will
     * probably be the most used field type.
     *
     * If the field is null, when the injection system
     * tries using it, an {@link ArrayList} will be
     * created and assigned to the field value.
     *
     * @throws SecurityException
     * @throws NoSuchFieldException
     */
    @Test
    public void testListInjection() throws NoSuchFieldException, SecurityException {
        CollectionCatcherServiceProvider collectionCatcher = new CollectionCatcherServiceProvider();
        Field injectionPoint = collectionCatcher.getClass().getDeclaredField("storageServices");
        Injection injectionField = new InjectionField(collectionCatcher, injectionPoint);

        // Verify that the injection type is the element type of the collection
        assertEquals(StorageService.class, injectionField.getInjectedServiceType());

        // Verify that the field isn't initially injected
        assertFalse(injectionField.isInjected());

        // Inject something not a storage service, this should be a no-op wrt. injection (ie. field still not injected)
        injectionField.doInject(helloServiceProvider.get());
        assertFalse(injectionField.isInjected());

        // Inject a storage service
        MemoryStorageServiceProvider memoryStorageServiceProvider = new MemoryStorageServiceProvider();
        injectionField.doInject(memoryStorageServiceProvider.get());
        assertTrue(injectionField.isInjected());

        // Inject another storage service
        DummyStorageServiceProvider dummyStorageProvider = new DummyStorageServiceProvider();
        injectionField.doInject(dummyStorageProvider.get());
        assertTrue(injectionField.isInjected());

        // Remove one of the storage services, field is still injected (removing an already removed service is a no-op)
        injectionField.doRetract(dummyStorageProvider.get());
        assertTrue(injectionField.isInjected());

        // Remove the same storage service again, field should still be injected
        injectionField.doRetract(dummyStorageProvider.get());
        assertTrue(injectionField.isInjected());

        // Remove the other storage service, field should no longer be injected
        injectionField.doRetract(memoryStorageServiceProvider.get());
        assertFalse(injectionField.isInjected());
    }

    /**
     * Unit test for injecting into a field that is a
     * collection value that is a {@link LinkedList}.
     *
     * If the field is null, a {@link LinkedList} will be created
     * and assigned as the field value.
     * @throws SecurityException
     * @throws NoSuchFieldException
     */
    @Test
    public void testLinkedListInjection() throws NoSuchFieldException, SecurityException {
        CollectionCatcherServiceProviderWithLinkedList collectionCatcher = new CollectionCatcherServiceProviderWithLinkedList();
        Field injectionPoint = collectionCatcher.getClass().getDeclaredField("storageServices");
        Injection injectionField = new InjectionField(collectionCatcher, injectionPoint);

        // Verify that the injection type is the element type of the collection
        assertEquals(StorageService.class, injectionField.getInjectedServiceType());

        // Verify that the field isn't initially injected
        assertFalse(injectionField.isInjected());

        // Inject something not a storage service, this should be a no-op wrt. injection (ie. field still not injected)
        injectionField.doInject(helloServiceProvider.get());
        assertFalse(injectionField.isInjected());

        // Inject a storage service
        MemoryStorageServiceProvider memoryStorageServiceProvider = new MemoryStorageServiceProvider();
        injectionField.doInject(memoryStorageServiceProvider.get());
        assertTrue(injectionField.isInjected());

        // Inject another storage service
        DummyStorageServiceProvider dummyStorageProvider = new DummyStorageServiceProvider();
        injectionField.doInject(dummyStorageProvider.get());
        assertTrue(injectionField.isInjected());

        // Remove one of the storage services, field is still injected (removing an already removed service is a no-op)
        injectionField.doRetract(dummyStorageProvider.get());
        assertTrue(injectionField.isInjected());

        // Remove the same storage service again, field should still be injected
        injectionField.doRetract(dummyStorageProvider.get());
        assertTrue(injectionField.isInjected());

        // Remove the other storage service, field should no longer be injected
        injectionField.doRetract(memoryStorageServiceProvider.get());
        assertFalse(injectionField.isInjected());
    }

    /**
     * Unit test for injecting into a field that is a
     * collection value that is a non-generic {@link List}.
     *
     * A non-generic collection won't work as an injection point
     * because there is no item type for the reflection to find
     * and listen for in OSGi (the item type is the sought for
     * service type).
     *
     * Actually: the default seems to be something that listens
     * for a {@link List} service.  Must try to make it consistent
     * for this.
     *
     * @throws SecurityException
     * @throws NoSuchFieldException
     */
    @Test
    public void testNonGenericListInjection() throws NoSuchFieldException, SecurityException {
        CollectionCatcherServiceProviderWithNonGenericList collectionCatcher = new CollectionCatcherServiceProviderWithNonGenericList();
        Field injectionPoint = collectionCatcher.getClass().getDeclaredField("storageServices");
        Injection injectionField = new InjectionField(collectionCatcher, injectionPoint);

        // Verify that the injection type is the field type itself.
        assertEquals(List.class, injectionField.getInjectedServiceType());

        // Verify that the field isn't initially injected
        assertFalse(injectionField.isInjected());

        // Inject something not a storage service, this should be a no-op wrt. injection (ie. field still not injected)
        injectionField.doInject(helloServiceProvider.get());
        assertFalse(injectionField.isInjected());

        // Inject a list
        @SuppressWarnings("rawtypes")
            List stringList = Arrays.asList("Hello world!");
        injectionField.doInject(stringList);
        assertTrue(injectionField.isInjected());

        // Remove the injection and the field is no longer injected
        injectionField.doRetract(stringList);
        assertFalse(injectionField.isInjected());
    }

    /**
     * Unit test for injecting into a field that is a an already initialized Set.
     * @throws SecurityException
     * @throws NoSuchFieldException
     */
    @Test
    public void testSetInjectionSetAlreadyPresent() throws NoSuchFieldException, SecurityException {
        CollectionCatcherServiceProviderWithSetInject collectionCatcher = new CollectionCatcherServiceProviderWithSetInject();
        Field injectionPoint = collectionCatcher.getClass().getDeclaredField("storageServices");
        Injection injectionField = new InjectionField(collectionCatcher, injectionPoint);

        // Verify that the injection type is the element type of the collection
        assertEquals(StorageService.class, injectionField.getInjectedServiceType());

        // Verify that the field isn't initially injected
        assertFalse(injectionField.isInjected());

        // Inject something not a storage service, this should be a no-op wrt. injection (ie. field still not injected)
        injectionField.doInject(helloServiceProvider.get());
        assertFalse(injectionField.isInjected());

        // Inject a storage service
        MemoryStorageServiceProvider memoryStorageServiceProvider = new MemoryStorageServiceProvider();
        injectionField.doInject(memoryStorageServiceProvider.get());
        assertTrue(injectionField.isInjected());

        // Inject another storage service
        DummyStorageServiceProvider dummyStorageProvider = new DummyStorageServiceProvider();
        injectionField.doInject(dummyStorageProvider.get());
        assertTrue(injectionField.isInjected());

        // Remove one of the storage services, field is still injected (removing an already removed service is a no-op)
        injectionField.doRetract(dummyStorageProvider.get());
        assertTrue(injectionField.isInjected());

        // Remove the same storage service again, field should still be injected
        injectionField.doRetract(dummyStorageProvider.get());
        assertTrue(injectionField.isInjected());

        // Remove the other storage service, field should no longer be injected
        injectionField.doRetract(memoryStorageServiceProvider.get());
        assertFalse(injectionField.isInjected());
    }

    /**
     * Unit test for injecting into a {@link Set} field that is null.
     *
     * A {@link HashSet} will be created and assigned as the field
     * value.
     * @throws SecurityException
     * @throws NoSuchFieldException
     */
    @Test
    public void testSetInjectionSetUninitialized() throws NoSuchFieldException, SecurityException {
        CollectionCatcherServiceProviderWithUninitializedSetInject collectionCatcher = new CollectionCatcherServiceProviderWithUninitializedSetInject();
        Field injectionPoint = collectionCatcher.getClass().getDeclaredField("storageServices");
        Injection injectionField = new InjectionField(collectionCatcher, injectionPoint);

        // Verify that the injection type is the element type of the collection
        assertEquals(StorageService.class, injectionField.getInjectedServiceType());

        // Verify that the field isn't initially injected
        assertFalse(injectionField.isInjected());

        // Inject something not a storage service, this should be a no-op wrt. injection (ie. field still not injected)
        injectionField.doInject(helloServiceProvider.get());
        assertFalse(injectionField.isInjected());

        // Inject a storage service
        MemoryStorageServiceProvider memoryStorageServiceProvider = new MemoryStorageServiceProvider();
        injectionField.doInject(memoryStorageServiceProvider.get());
        assertTrue(injectionField.isInjected());

        // Inject another storage service
        DummyStorageServiceProvider dummyStorageProvider = new DummyStorageServiceProvider();
        injectionField.doInject(dummyStorageProvider.get());
        assertTrue(injectionField.isInjected());

        // Remove one of the storage services, field is still injected (removing an already removed service is a no-op)
        injectionField.doRetract(dummyStorageProvider.get());
        assertTrue(injectionField.isInjected());

        // Remove the same storage service again, field should still be injected
        injectionField.doRetract(dummyStorageProvider.get());
        assertTrue(injectionField.isInjected());

        // Remove the other storage service, field should no longer be injected
        injectionField.doRetract(memoryStorageServiceProvider.get());
        assertFalse(injectionField.isInjected());
    }

    /**
     * Unit test for injecting into a {@link TreeSet} field that is null.
     *
     * A {@link HashSet} will fail here, so a {@link TreeSet} will be created and assigned as the field
     * value.
     * @throws SecurityException
     * @throws NoSuchFieldException
     */
    @Test
    public void testSetInjectionTreeSetUninitialized() throws NoSuchFieldException, SecurityException {
        CollectionCatcherServiceProviderWithUninitializedTreeSetInject collectionCatcher = new CollectionCatcherServiceProviderWithUninitializedTreeSetInject();
        Field injectionPoint = collectionCatcher.getClass().getDeclaredField("storageServices");
        Injection injectionField = new InjectionField(collectionCatcher, injectionPoint);

        // Verify that the injection type is the element type of the collection
        assertEquals(StorageService.class, injectionField.getInjectedServiceType());

        // Verify that the field isn't initially injected
        assertFalse(injectionField.isInjected());

        // Inject something not a storage service, this should be a no-op wrt. injection (ie. field still not injected)
        injectionField.doInject(helloServiceProvider.get());
        assertFalse(injectionField.isInjected());

        // Inject a storage service
        MemoryStorageServiceProvider memoryStorageServiceProvider = new MemoryStorageServiceProvider();
        injectionField.doInject(memoryStorageServiceProvider.get());
        assertTrue(injectionField.isInjected());

        // Inject another storage service
        DummyStorageServiceProvider dummyStorageProvider = new DummyStorageServiceProvider();
        injectionField.doInject(dummyStorageProvider.get());
        assertTrue(injectionField.isInjected());

        // Remove one of the storage services, field is still injected (removing an already removed service is a no-op)
        injectionField.doRetract(dummyStorageProvider.get());
        assertTrue(injectionField.isInjected());

        // Remove the same storage service again, field should still be injected
        injectionField.doRetract(dummyStorageProvider.get());
        assertTrue(injectionField.isInjected());

        // Remove the other storage service, field should no longer be injected
        injectionField.doRetract(memoryStorageServiceProvider.get());
        assertFalse(injectionField.isInjected());
    }

    /**
     * Unit tests for {@link InjectionField#isOptional()}.
     *
     * @throws NoSuchFieldException
     * @throws SecurityException
     */
    @Test
    public void testIsOptional() throws NoSuchFieldException, SecurityException {
        Field injectionPoint = helloService2Provider.getClass().getDeclaredField("helloService");
        Field nonOptionalCollectionInject = getClass().getDeclaredField("nonOptionalCollection");
        Field optionalCollectionInject = getClass().getDeclaredField("optionalCollection");
        Field nonOptionalNamedInject = getClass().getDeclaredField("nonOptionalNamed");
        Field optionalNamedInject = getClass().getDeclaredField("optionalNamed");
        Field optionalUnnamedInject = getClass().getDeclaredField("optionalUnnamed");

        Injection injectionField = new InjectionField(helloService2Provider, injectionPoint);
        assertFalse(injectionField.isOptional());

        Injection nonOptionalCollectionInjection = new InjectionField(this, nonOptionalCollectionInject);
        assertFalse(nonOptionalCollectionInjection.isOptional());

        Injection optionalCollectionInjection = new InjectionField(this, optionalCollectionInject);
        assertTrue(optionalCollectionInjection.isOptional());

        Injection nonOptionalNamedInjection = new InjectionField(this, nonOptionalNamedInject);
        assertFalse(nonOptionalNamedInjection.isOptional());

        Injection optionalNamedInjection = new InjectionField(this, optionalNamedInject);
        assertTrue(optionalNamedInjection.isOptional());

        Injection optionalUnnamedInjection = new InjectionField(this, optionalUnnamedInject);
        assertTrue(optionalUnnamedInjection.isOptional());
    }

}
