package no.steria.osgi.jsr330activator;

import javax.inject.Inject;
import javax.inject.Provider;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * An OSGi {@link BundleActivator} implementation, that will scan the
 * current bundle's class path for JSR330 {@link Inject} annotations
 * and {@link Provider} implementations.
 *
 * On {@link #start(BundleContext)}, this activator will then set up listeners
 * for all injected services and register services for all providers that don't
 * require injections.
 *
 * When providers requiring injections, receive all of their required injections,
 * they will be used to create service implementations that will be registered
 * with the bundle context.
 *
 * When required injections are removed from the OSGi service registry,
 * the services requiring them will be unregistered.
 *
 * On {@link #stop(BundleContext)}, this activator will unregister
 * all services still running.
 *
 * @author Steinar Bang
 *
 */
public class Jsr330Activator implements BundleActivator {

    public void start(BundleContext context) throws Exception {
        // TODO add activation code here
    }

    public void stop(BundleContext context) throws Exception {
        // TODO add deactivation code here
    }

}
