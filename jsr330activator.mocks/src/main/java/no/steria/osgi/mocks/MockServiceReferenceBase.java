package no.steria.osgi.mocks;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * Default implementations for all methods of {@link BundleContext}.
 *
 * The actual mock class inherits this class, and only needs replace
 * those methods needed by the tests.
 *
 * @author Steinar Bang
 *
 */
public class MockServiceReferenceBase<S> implements ServiceReference<S> {

    public Object getProperty(String key) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public String[] getPropertyKeys() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Bundle getBundle() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Bundle[] getUsingBundles() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public boolean isAssignableTo(Bundle bundle, String className) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public int compareTo(Object reference) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

}
