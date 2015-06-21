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
import org.osgi.framework.wiring.BundleWiring;

/**
 * Mock implementation of {@link BundleContext}.
 *
 * @author Steinar Bang
 *
 */
public class MockBundleContext extends MockBundleContextBase {

    MockBundle bundle;
    Map<String, Collection<ServiceReference<?>>> serviceRegistrations = new HashMap<String, Collection<ServiceReference<?>>>();
    Map<ServiceReference<?>, Object> serviceImplementations = new HashMap<ServiceReference<?>, Object>();
    Map<String, List<ServiceListener>> filteredServiceListeners = new HashMap<String, List<ServiceListener>>();

    /**
     * Constructor used when it is necessary to create the bundle in
     * the test, e.g. when providing a {@link BundleWiring} object.
     *
     * @param bundle a mock {@link Bundle} object.
     */
    public MockBundleContext(MockBundle bundle) {
        this.bundle = bundle;
    }

    /**
     * The typical constructor to use in a simple test.
     * Will create a {@link MockBundle} without a {@link BundleWiring}
     * object.
     */
    public MockBundleContext() {
        this(new MockBundle());
    }

    /**
     * Access this context's {@link Bundle} object.
     *
     * @return a {@link Bundle} reference
     */
    @Override
    public Bundle getBundle() {
        return bundle;
    }

    /**
     * Register a service with the bundle context.
     *
     * @param clazz the fully qualified name of the interface of the service being registered
     * @param service the object referencing the service
     * @param properties a name/value map for names and values that can be used to qualify the service when multiple implementations are availabl
     * @return a {@link ServiceRegistration} object that can be used to unregister the service later
     */
    @Override
    public ServiceRegistration<?> registerService(String clazz, Object service, Dictionary<String, ?> properties) {
        MockServiceReference<Object> serviceReference = new MockServiceReference<Object>(getBundle(), properties);
        if (serviceRegistrations.get(clazz) == null) { serviceRegistrations.put(clazz, new ArrayList<ServiceReference<?>>()); }
        serviceRegistrations.get(clazz).add(serviceReference);
        serviceImplementations.put(serviceReference, service);
        notifyListenersAboutNewService(clazz, serviceReference);
        return new MockServiceRegistration<Object>(this, serviceReference);
    }

    /**
     * Get all {@link ServiceReference} object matching a class name and a filter.
     *
     * <p><i>Note:</i> neither the clazz nor the filter argument is handled in any way
     * If this is needed to make tests run as expected it must be implemented
     * For now, just return all of the references in the mock.
     *
     * @param a fully qualified class name
     * @param a filter expression matching a service and its parameters
     * @return an array of {@link ServiceReference} objects implementing the matching service, or an empty array if none can be found
     */
    @Override
    public ServiceReference<?>[] getServiceReferences(String clazz, String filter) throws InvalidSyntaxException {
        List<ServiceReference<?>> serviceReferences = new ArrayList<ServiceReference<?>>();
        for (Entry<String, Collection<ServiceReference<?>>> serviceRegistrationEntry : serviceRegistrations.entrySet()) {
            for (ServiceReference<?> serviceRegistration : serviceRegistrationEntry.getValue()) {
                serviceReferences.add(serviceRegistration);
            }
        }

        return serviceReferences.toArray(new ServiceReference<?>[serviceReferences.size()]);
    }

    /**
     * Get all {@link ServiceReference} objects matching the fully qualified
     * class name given as an argument
     */
    @Override
    public ServiceReference<?> getServiceReference(String clazz) {
    	Collection<ServiceReference<?>> servrefs = serviceRegistrations.get(clazz);
    	if (servrefs == null || servrefs.size() == 0) {
            return null;
    	}

        return servrefs.iterator().next();
    }

    /**
     * Get the service a {@link ServiceReference} is referring to.
     *
     * @param a {@link ServiceReference} for a registered service
     * @return the object implementing the service
     */
    @SuppressWarnings("unchecked")
    @Override
    public <S> S getService(ServiceReference<S> reference) {
        return (S) serviceImplementations.get(reference);
    }

    /**
     * Tell the service registration that a particular service
     * is no longer being used.
     *
     * @param a {@link ServiceReference} for a registered service
     * @return true if the release succeeds (this mack always returns true).
     */
    @Override
    public boolean ungetService(ServiceReference<?> reference) {
        // Always succeeds.  Called when releasing a reference
        return true;
    }


    /**
     * Retract a service registration (a provided service).
     *
     * @param unregisteredServiceRegistration a {@link ServiceRegistration} for the service that should be unregistered
     * @return true if unregistration is successful and false if unregistration fails (e.g. if the service can't be found).
     */
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

    /**
     * Register a listener for a service, using a filter expression.
     * @param listener the listener callback object to register
     * @filter a filter expression matching a service
     */
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

    /**
     * Remove a listener object
     *
     * @param listener the listener callback object to remove
     */
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
