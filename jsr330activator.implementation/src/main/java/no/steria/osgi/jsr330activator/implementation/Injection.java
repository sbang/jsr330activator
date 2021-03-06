package no.steria.osgi.jsr330activator.implementation;

import javax.inject.Named;

import org.osgi.framework.BundleContext;

/**
 * Defines an injection point in a provider class.
 * Called from the bundle activator (or actually,
 * from a listener callback), when the service
 * becomes available.
 *
 * @author Steinar Bang
 *
 */
interface Injection {

    /**
     * Get the type of the injected service
     *
     * @return a class object defining the injected service.
     */
    Class<?> getInjectedServiceType();

    /**
     * Get the value of the {@link Named} annotation on the injection member.
     *
     * @return the {@link Named#value()} or null if the member has no Named annotation
     */
    String getNamedValue();

    /**
     * Determine if an Injection is optional.
     *
     * @return true if the injection is optional, false if it isn't optional.
     */
    boolean isOptional();

    /**
     * Used to determine if the injection point is currently in an
     * injected state.
     *
     * @return true if this injection has been set to a valid value
     */
    boolean isInjected();

    /**
     * Set the value of the injection point.  If the value can't be
     * set for any reason, the method is a no-op.
     *
     * @param service a valid OSGi service that is of the correct type for the injection.
     */
    void doInject(Object service);

    /**
     * Unset the injected value, if this is the same object that was
     * originally injected.  If this is false or if the operation
     * fails, the method is a no-op.
     *
     * Called when receiving an unregistration event about the service.
     * @param service the service to retract
     */
    void doRetract(Object service);

    /**
     * Will set up an OSGi listener for the injected service, so
     * that it will be injected when the service appears in OSGi
     * and retracted when the service disappears from OSGi.
     *
     * @param bundleContext the object to register the listener with
     * @param providerAdapter the object to notify after an injection has been received and set
     */
    void setupListener(BundleContext bundleContext, ProviderAdapter providerAdapter);

    /**
     * Release this injection when stopping the provider.
     *
     * @param context
     */
    void unGet(BundleContext context);

}
