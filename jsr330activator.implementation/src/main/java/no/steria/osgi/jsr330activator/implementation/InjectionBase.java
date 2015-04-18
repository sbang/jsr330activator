package no.steria.osgi.jsr330activator.implementation;

import org.osgi.framework.BundleContext;
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
    private ServiceReference<?> serviceReference;

    public void setupListener(final BundleContext bundleContext, final ProviderAdapter providerAdapter) {
        serviceListener = new ServiceListener() {

                public void serviceChanged(ServiceEvent event) {
                    ServiceReference<?> sr = event.getServiceReference();
                    switch(event.getType()) {
                      case ServiceEvent.REGISTERED:
                        serviceReference = sr;
                        Object service = bundleContext.getService(sr);
                        doInject(service);
                        providerAdapter.checkInjectionsAndRegisterServiceIfSatisfied(bundleContext);
                        break;
                      case ServiceEvent.UNREGISTERING:
                        doRetract();
                        providerAdapter.checkInjectionsAndUnregisterServiceIfNotSatisfied(bundleContext);
                        break;
                      default:
                        break;
                    }
                }
            };

        String filter = "(objectclass=" + getInjectedServiceType().getName() + ")";
        try {
            bundleContext.addServiceListener(serviceListener, filter);
            fakeRegisteredServiceEventForExistingServices(bundleContext, serviceListener, filter);
        } catch (InvalidSyntaxException e) {
        }
    }

    private void fakeRegisteredServiceEventForExistingServices(final BundleContext bundleContext, ServiceListener sl, String filter) throws InvalidSyntaxException {
        ServiceReference<?>[] servicesPresent = bundleContext.getServiceReferences((String)null, filter);
        for (ServiceReference<?> serviceReference : servicesPresent) {
            sl.serviceChanged(new ServiceEvent(ServiceEvent.REGISTERED, serviceReference));
        }
    }

    public void unGet(BundleContext bundleContext) {
        // Remove listener first so that this class won't receive a new event
        bundleContext.removeServiceListener(serviceListener);

        // Release the injected service
        bundleContext.ungetService(serviceReference);
    }

}
