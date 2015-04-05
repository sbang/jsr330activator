package no.steria.osgi.mocks;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

/**
 * Mock implementation of {@link BundleContext}.
 *
 * @author Steinar Bang
 *
 */
public class MockBundleContext extends MockBundleContextBase {
    MockBundle bundle;
    Map<String, ServiceReference<?>> serviceRegistrations = new HashMap<String, ServiceReference<?>>();
    Map<ServiceReference<?>, Object> serviceImplementations = new HashMap<ServiceReference<?>, Object>();

    public MockBundleContext(MockBundle bundle) {
        this.bundle = bundle;
    }

    public MockBundleContext() {
        this(new MockBundle());
    }

    @Override
    public Bundle getBundle() {
        return bundle;
    }

    @Override
    public ServiceRegistration<?> registerService(String clazz, Object service, Dictionary<String, ?> properties) {
        MockServiceReference<Object> serviceReference = new MockServiceReference<Object>(getBundle());
        serviceRegistrations.put(clazz, serviceReference);
        serviceImplementations.put(serviceReference, service);
        return null; // Don't use the return value in any of my tests
    }

    @Override
    public ServiceReference<?> getServiceReference(String clazz) {
        return serviceRegistrations.get(clazz);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <S> S getService(ServiceReference<S> reference) {
        return (S) serviceImplementations.get(reference);
    }

}
