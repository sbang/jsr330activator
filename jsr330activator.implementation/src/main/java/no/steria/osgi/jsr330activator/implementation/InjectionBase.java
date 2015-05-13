package no.steria.osgi.jsr330activator.implementation;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

/**
 * Contains methods shared between {@link Injection} implementations.
 *
 * @author Steinar Bang
 *
 */
abstract class InjectionBase implements Injection {

    private ServiceListener serviceListener;
    private List<ServiceReference<?>> serviceReferences = new ArrayList<ServiceReference<?>>();

    public void setupListener(final BundleContext bundleContext, final ProviderAdapter providerAdapter) {
        serviceListener = new ServiceListener() {

                public void serviceChanged(ServiceEvent event) {
                    ServiceReference<?> sr = event.getServiceReference();
                    Object service = bundleContext.getService(sr);
                    switch(event.getType()) {
                      case ServiceEvent.REGISTERED:
                    	if (!serviceReferences.contains(sr)) { serviceReferences.add(sr); }
                        doInject(service);
                        providerAdapter.checkInjectionsAndRegisterServiceIfSatisfied(bundleContext);
                        break;
                      case ServiceEvent.UNREGISTERING:
                    	serviceReferences.remove(sr);
                        doRetract(service);
                        providerAdapter.checkInjectionsAndUnregisterServiceIfNotSatisfied(bundleContext);
                        break;
                      default:
                        break;
                    }
                }
            };

        String filter = "(" + Constants.OBJECTCLASS + "=" + getInjectedServiceType().getName() + ")";
        try {
            bundleContext.addServiceListener(serviceListener, filter);
            fakeRegisteredServiceEventForExistingServices(bundleContext, serviceListener, filter);
        } catch (InvalidSyntaxException e) {
        }
    }

    private void fakeRegisteredServiceEventForExistingServices(final BundleContext bundleContext, ServiceListener sl, String filter) throws InvalidSyntaxException {
        ServiceReference<?>[] servicesPresent = bundleContext.getServiceReferences((String)null, filter);
        if (servicesPresent != null) {
            for (ServiceReference<?> serviceReference : servicesPresent) {
                sl.serviceChanged(new ServiceEvent(ServiceEvent.REGISTERED, serviceReference));
            }
        }
    }

    public void unGet(BundleContext bundleContext) {
        // Remove listener first so that this class won't receive a new event
        bundleContext.removeServiceListener(serviceListener);

        // Release the injected service
        for (ServiceReference<?> serviceReference : serviceReferences) {
            bundleContext.ungetService(serviceReference);
        }
    }

}
