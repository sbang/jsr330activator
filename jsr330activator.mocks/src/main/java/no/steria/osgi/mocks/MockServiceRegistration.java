package no.steria.osgi.mocks;

import java.util.Dictionary;

import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

/**
 * A mock for the object returned when registering a service with a
 * BundleContext.  This object can then be used when unregistering
 * the service with the BundleContext.
 *
 * <p>This object is usually not created when writing unit tests, it
 * is returned when registering a service with a {@link MockBundleContext}.
 *
 * @author Steinar Bang
 *
 * @param <S> the type of the service being registered.
 */
public class MockServiceRegistration<S> implements ServiceRegistration<S> {
    MockBundleContext bundleContext;
    ServiceReference<S> serviceReference;
    boolean alreadyUnregistered = false;

    public MockServiceRegistration(MockBundleContext bundleContext, ServiceReference<S> serviceReference) {
        this.bundleContext = bundleContext;
        this.serviceReference = serviceReference;
    }

    public ServiceReference<S> getReference() {
        return serviceReference;
    }

    public void setProperties(Dictionary<String, ?> properties) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void unregister() {
    	if (alreadyUnregistered) {
            throw new IllegalStateException("Service already unregistered.");
    	}

    	bundleContext.unregisterService(this);
    	alreadyUnregistered = true;
    }

}
