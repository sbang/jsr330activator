/**
 * This package contains an implementation of an OSGi {@link BundleActivator}
 * that will use reflection toscan the bundle for classes implementing the
 * {@link Provider} interface.
 * The BundleActivator will create a single instance for each of the
 * providers found, and call the {@link Provider#get()} method on
 * each instantiated provider, and register the object returned by the get()
 * method as an OSGi service.
 *
 * @author Steinar Bang
 *
 */
package no.steria.osgi.jsr330activator;
