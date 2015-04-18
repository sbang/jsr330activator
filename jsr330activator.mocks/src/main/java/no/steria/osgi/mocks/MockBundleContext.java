package no.steria.osgi.mocks;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

/**
 * Mock implementation of {@link BundleContext}.
 *
 * @author Steinar Bang
 *
 */
public class MockBundleContext extends MockBundleContextBase {
    @Override
    public ServiceReference<?>[] getServiceReferences(String clazz, String filter) throws InvalidSyntaxException {
        // Note: neither the clazz nor the filter argument is handled in any way
    	// If this is needed to make tests run as expected it must be implemented
    	// For now, just return all of the references in the mock.
    	ServiceReference<?>[] serviceReferences = new ServiceReference<?>[serviceRegistrations.size()];
    	int i = 0;
    	for (Entry<String, ServiceReference<?>> serviceRegistration : serviceRegistrations.entrySet()) {
            serviceReferences[i] = serviceRegistration.getValue();
            ++i;
        }

    	return serviceReferences;
    }

    MockBundle bundle;
    Map<String, ServiceReference<?>> serviceRegistrations = new HashMap<String, ServiceReference<?>>();
    Map<ServiceReference<?>, Object> serviceImplementations = new HashMap<ServiceReference<?>, Object>();
    Map<String, List<ServiceListener>> filteredServiceListeners = new HashMap<String, List<ServiceListener>>();

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
        notifyListenersAboutNewService(clazz, serviceReference);
        return new MockServiceRegistration<Object>(this, serviceReference);
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

    @Override
    public boolean ungetService(ServiceReference<?> reference) {
        // Always succeeds.  Called when releasing a reference
        return true;
    }

    public boolean unregisterService(MockServiceRegistration<?> unregisteredServiceRegistration) {
        boolean unregistrationSuccess = true;
        ServiceReference<?> serviceReference = unregisteredServiceRegistration.getReference();
        if (serviceRegistrations.containsValue(serviceReference)) {
            List<String> serviceClassNames = new ArrayList<String>();
            for (Entry<String, ServiceReference<?>> serviceRegistration : serviceRegistrations.entrySet()) {
                if (serviceReference.equals(serviceRegistration.getValue())) {
                    serviceClassNames.add(serviceRegistration.getKey());
                }
            }

            for (String serviceClassName : serviceClassNames) {
                serviceRegistrations.remove(serviceClassName);
            	notifyListenersAboutRemovedService(serviceClassName, serviceReference);
            }
        } else {
            unregistrationSuccess = false;
        }

        if (serviceImplementations.containsKey(serviceReference)) {
            serviceImplementations.remove(serviceReference);
        } else {
            unregistrationSuccess = false;
        }

        return unregistrationSuccess;
    }

    @Override
    public void addServiceListener(ServiceListener listener, String filter) throws InvalidSyntaxException {
        if (null == filteredServiceListeners.get(filter)) {
            filteredServiceListeners.put(filter, new ArrayList<ServiceListener>(1));
        }

        filteredServiceListeners.get(filter).add(listener);
    }

    private void notifyListenersAboutNewService(String clazz, MockServiceReference<Object> serviceReference) {
        String filter = "(objectclass=" + clazz + ")";
        List<ServiceListener> filteredServiceListenerList = filteredServiceListeners.get(filter);
        if (null != filteredServiceListenerList) {
            ServiceEvent newServiceEvent = new ServiceEvent(ServiceEvent.REGISTERED, serviceReference);
            for (ServiceListener serviceListener : filteredServiceListenerList) {
                serviceListener.serviceChanged(newServiceEvent);
            }
        }
    }

    private void notifyListenersAboutRemovedService(String clazz, ServiceReference<?> serviceReference) {
        String filter = "(objectclass=" + clazz + ")";
        List<ServiceListener> filteredServiceListenerList = filteredServiceListeners.get(filter);
        if (null != filteredServiceListenerList) {
            ServiceEvent newServiceEvent = new ServiceEvent(ServiceEvent.UNREGISTERING, serviceReference);
            for (ServiceListener serviceListener : filteredServiceListenerList) {
                serviceListener.serviceChanged(newServiceEvent);
            }
        }
    }

    @Override
    public void removeServiceListener(ServiceListener listener) {
        // Note: if unfiltered service listeners are implemented
        // This method needs to look for, and remove, the listener there
        // as well

        // Go through all of the filters, and for each filter
        // remove the listener from the filter's list.
        for(Entry<String, List<ServiceListener>> filteredList : filteredServiceListeners.entrySet()) {
            filteredList.getValue().remove(listener);
        }
    }

}
