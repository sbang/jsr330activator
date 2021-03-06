package no.steria.osgi.jsr330activator.implementation;

import static org.junit.Assert.*;

import java.lang.reflect.Method;

import javax.inject.Inject;
import javax.inject.Named;

import no.steria.osgi.jsr330activator.Optional;
import no.steria.osgi.jsr330activator.testbundle.HelloService;
import no.steria.osgi.jsr330activator.testbundle.implementation.AddInjectionsServiceProvider;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link InjectionMethod}.
 *
 * @author Steinar Bang
 *
 */
public class InjectionMethodTest {

    private String stringService = "0";
    private AddInjectionsServiceProvider addInjectionsServiceProvider;

    @SuppressWarnings("unused")
    private void dummy(String s) {
    	// Used to get a Method reflection object in test.
    }

    @Before
    public void setUp() throws Exception {
        addInjectionsServiceProvider = new AddInjectionsServiceProvider();
    }

    /**
     * Unit tests for {@link InjectionMethod#isInjected()}.
     *
     * @throws NoSuchMethodException
     * @throws SecurityException
     */
    @Test
    public void testIsInjected() throws NoSuchMethodException, SecurityException {
        Method injectionPoint = addInjectionsServiceProvider.getClass().getDeclaredMethod("setStringValue", String.class);

        Injection injectionMethod = new InjectionMethod(addInjectionsServiceProvider, injectionPoint);
        assertFalse(injectionMethod.isInjected());

        // Inject a real HelloService
        injectionMethod.doInject(stringService);
        assertTrue(injectionMethod.isInjected());

        // Retract the service and check the injected state
        injectionMethod.doRetract(stringService);
        assertFalse(injectionMethod.isInjected());
    }

    /**
     * Corner case unit test for {@link InjectionMethod#doInject(Object)}.
     *
     * Test that trying to inject a service of the wrong type results
     * in no injection.
     *
     * @throws SecurityException
     * @throws NoSuchMethodException
     */
    @Test
    public void testDoInjectWrongType() throws NoSuchMethodException, SecurityException  {
        Method injectionPoint = addInjectionsServiceProvider.getClass().getDeclaredMethod("setStringValue", String.class);

        // Create InjectionMethod and verify uninjected state.
        Injection injectionMethod = new InjectionMethod(addInjectionsServiceProvider, injectionPoint);
        assertFalse(injectionMethod.isInjected());

        // Try injection something other than the expected type and verify
        // that the InjectionMethod stays uninjected.
        Integer notAStringService = 4;
        injectionMethod.doInject(notAStringService);
        assertFalse(injectionMethod.isInjected());
    }

    /**
     * Corner case unit test for {@link InjectionMethod#doInject(Object)}.
     *
     * Test that using a Method belonging to a different type results
     * in no injection.
     *
     * @throws SecurityException
     * @throws NoSuchMethodException
     */
    @Test
    public void testDoInjectMethodBelongsToDifferentType() throws NoSuchMethodException, SecurityException  {
        Method injectionPointNotAMethodOnProvider = this.getClass().getDeclaredMethod("dummy", String.class);

        // Create InjectionMethod and verify uninjected state.
        Injection injectionMethod = new InjectionMethod(addInjectionsServiceProvider, injectionPointNotAMethodOnProvider);
        assertFalse(injectionMethod.isInjected());

        // Try injection something other than the expected type and verify
        // that the InjectionMethod stays uninjected.
        String notAHelloService = "This is not HelloService!";
        injectionMethod.doInject(notAHelloService);
        assertFalse(injectionMethod.isInjected());
    }

    /**
     * Unit test for {@link InjectionMethod#doInject(Object)}.
     *
     * @throws SecurityException
     * @throws NoSuchMethodException
     */
    @Test
    public void testDoRetract() throws NoSuchMethodException, SecurityException {
        Method injectionPoint = addInjectionsServiceProvider.getClass().getDeclaredMethod("setStringValue", String.class);
        Injection injectionMethod = new InjectionMethod(addInjectionsServiceProvider, injectionPoint);

        // Inject a real HelloService
        injectionMethod.doInject(stringService);
        assertTrue(injectionMethod.isInjected());

        // Retract the service and verify that it's gone
        injectionMethod.doRetract(stringService);
        assertFalse(injectionMethod.isInjected());
    }

    // Methods used for "isOptional" tests

    @Inject
    @Named("named")
    void setNonOptionalNamed(HelloService service) { }

    @Inject
    @Optional
    @Named("optionalnamed")
    void setOptionalNamed(HelloService service) { }

    @Inject
    @Optional
    void setOptionalUnnamed(HelloService service) { }

    /**
     * Unit tests for {@link InjectionMethod#isOptional()}.
     *
     * @throws NoSuchMethodException
     * @throws SecurityException
     */
    @Test
    public void testIsOptional() throws NoSuchMethodException, SecurityException {
        Method injectionPoint = addInjectionsServiceProvider.getClass().getDeclaredMethod("setStringValue", String.class);
        Method nonOptionalNamedInject = getClass().getDeclaredMethod("setNonOptionalNamed", HelloService.class);
        Method optionalNamedInject = getClass().getDeclaredMethod("setOptionalNamed", HelloService.class);
        Method optionalUnnamedInject = getClass().getDeclaredMethod("setOptionalUnnamed", HelloService.class);

        Injection injectionMethod = new InjectionMethod(addInjectionsServiceProvider, injectionPoint);
        assertFalse(injectionMethod.isOptional());

        Injection nonOptionalNamedInjection = new InjectionMethod(this, nonOptionalNamedInject);
        assertFalse(nonOptionalNamedInjection.isOptional());

        Injection optionalNamedInjection = new InjectionMethod(this, optionalNamedInject);
        assertTrue(optionalNamedInjection.isOptional());

        Injection optionalUnnamedInjection = new InjectionMethod(this, optionalUnnamedInject);
        assertTrue(optionalUnnamedInjection.isOptional());
    }

}
