package no.steria.osgi.mocks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
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
        // Note: the clazz argument is not handled in any way
    	// If this is needed to make tests run as expected it must be implemented
    	if (filter != null) {
            Collection<ServiceReference<?>> serviceReferences = filteredServiceRegistrations.get(filter);
            if (serviceReferences != null) {
            	return serviceReferences.toArray(new ServiceReference<?>[serviceReferences.size()]);
            }

            return new ServiceReference<?>[0];
    	}

    	// No filter, just return all of the references in the mock.
    	List<ServiceReference<?>> serviceReferences = new ArrayList<ServiceReference<?>>();
    	for (Entry<String, Collection<ServiceReference<?>>> serviceRegistrationEntry : serviceRegistrations.entrySet()) {
            for (ServiceReference<?> serviceRegistration : serviceRegistrationEntry.getValue()) {
                serviceReferences.add(serviceRegistration);
            }
        }

    	return serviceReferences.toArray(new ServiceReference<?>[serviceReferences.size()]);
    }

    MockBundle bundle;
    Map<String, Collection<ServiceReference<?>>> serviceRegistrations = new HashMap<String, Collection<ServiceReference<?>>>();
    Map<String, Collection<ServiceReference<?>>> filteredServiceRegistrations = new HashMap<String, Collection<ServiceReference<?>>>();
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
        MockServiceReference<Object> serviceReference = new MockServiceReference<Object>(getBundle(), properties);
        if (serviceRegistrations.get(clazz) == null) { serviceRegistrations.put(clazz, new ArrayList<ServiceReference<?>>()); }
        serviceRegistrations.get(clazz).add(serviceReference);
        serviceImplementations.put(serviceReference, service);
        notifyListenersAboutNewService(clazz, serviceReference);
        return new MockServiceRegistration<Object>(this, serviceReference);
    }

    @Override
    public ServiceReference<?> getServiceReference(String clazz) {
    	Collection<ServiceReference<?>> servrefs = serviceRegistrations.get(clazz);
    	if (servrefs == null || servrefs.size() == 0) {
            return null;
    	}

        return servrefs.iterator().next();
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
        String serviceClassName = findServiceClassName(serviceReference);
        if (serviceRegistrations.containsKey(serviceClassName)) {
            serviceRegistrations.get(serviceClassName).remove(serviceReference);
            if (serviceRegistrations.get(serviceClassName).isEmpty()) {
                // Last implementation gone, remove from registry
                serviceRegistrations.remove(serviceClassName);
            }

            notifyListenersAboutRemovedService(serviceClassName, serviceReference);
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

    private String findServiceClassName(ServiceReference<?> serviceReference) {
        for (Entry<String, Collection<ServiceReference<?>>> serviceRegistrationEntry : serviceRegistrations.entrySet()) {
            for (ServiceReference<?> serviceRegistration : serviceRegistrationEntry.getValue()) {
                if (serviceReference.equals(serviceRegistration)) {
                    return serviceRegistrationEntry.getKey();
                }
            }
        }

        return "";
    }

    @Override
    public void addServiceListener(ServiceListener listener, String filter) throws InvalidSyntaxException {
        if (null == filteredServiceListeners.get(filter)) {
            filteredServiceListeners.put(filter, new ArrayList<ServiceListener>(1));
        }

        filteredServiceListeners.get(filter).add(listener);
    }

    private void notifyListenersAboutNewService(String clazz, MockServiceReference<Object> serviceReference) {
        String filter = "(" + Constants.OBJECTCLASS+ "=" + clazz + ")";
        // To make a proper mock of the notification all possible
        // permutations of the property keys should be tried as
        // a filter, but for the current testing there is only
        // the single key "id" so running through the keys one
        // by one should suffice
        for (String servicePropertyKey : serviceReference.getPropertyKeys()) {
            String filterWithProperty = "(&" + filter + "(" + servicePropertyKey + "=" + serviceReference.getProperty(servicePropertyKey) + "))";
            notifyFilteredListenersAboutNewService(serviceReference, filterWithProperty);
        }

        // Also notify any undecorated listeners
        notifyFilteredListenersAboutNewService(serviceReference, filter);
    }

    private void notifyFilteredListenersAboutNewService(MockServiceReference<Object> serviceReference, String filter) {
        List<ServiceListener> filteredServiceListenerList = filteredServiceListeners.get(filter);
        if (null != filteredServiceListenerList) {
            ServiceEvent newServiceEvent = new ServiceEvent(ServiceEvent.REGISTERED, serviceReference);
            for (ServiceListener serviceListener : filteredServiceListenerList) {
                serviceListener.serviceChanged(newServiceEvent);
            }
        }

        addFilteredServiceRegistration(serviceReference, filter);
    }

    private void addFilteredServiceRegistration(MockServiceReference<Object> serviceReference, String filter) {
        if (filteredServiceRegistrations.get(filter) == null) { filteredServiceRegistrations.put(filter, new ArrayList<ServiceReference<?>>()); }
        filteredServiceRegistrations.get(filter).add(serviceReference);
    }

    private void notifyListenersAboutRemovedService(String clazz, ServiceReference<?> serviceReference) {
        String filter = "(" + Constants.OBJECTCLASS+ "=" + clazz + ")";
        // To make a proper mock of the notification all possible
        // permutations of the property keys should be tried as
        // a filter, but for the current testing there is only
        // the single key "id" so running through the keys one
        // by one should suffice
        for (String servicePropertyKey : serviceReference.getPropertyKeys()) {
            String filterWithProperty = "(&" + filter + "(" + servicePropertyKey + "=" + serviceReference.getProperty(servicePropertyKey) + "))";
            notifyFilteredListenersAboutRemovedService(serviceReference, filterWithProperty);
        }

        // Also notify any undecorated listeners
        notifyFilteredListenersAboutRemovedService(serviceReference, filter);
    }

    private void notifyFilteredListenersAboutRemovedService(ServiceReference<?> serviceReference, String filter) {
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
