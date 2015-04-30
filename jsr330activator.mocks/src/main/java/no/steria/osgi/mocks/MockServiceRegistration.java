package no.steria.osgi.mocks;

import java.util.Dictionary;

import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

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
