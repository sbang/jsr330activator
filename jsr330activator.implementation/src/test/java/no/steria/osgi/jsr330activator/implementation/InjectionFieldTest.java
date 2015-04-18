package no.steria.osgi.jsr330activator.implementation;

import static org.junit.Assert.*;

import java.lang.reflect.Field;

import no.steria.osgi.jsr330activator.testbundle.HelloService;
import no.steria.osgi.jsr330activator.testbundle.implementation.HelloService2Provider;
import no.steria.osgi.jsr330activator.testbundle.implementation.HelloServiceProvider;

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
        injectionField.doRetract();
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
        injectionField.doRetract();
        assertFalse(injectionField.isInjected());
    }

}
