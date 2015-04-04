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

    private static final String[] emptyStringArray = new String[0];
    private static final Bundle[] emptyBundleArray = new Bundle[0];

    public Object getProperty(String key) {
        // TODO Auto-generated method stub
        return null;
    }

    public String[] getPropertyKeys() {
        // TODO Auto-generated method stub
        return emptyStringArray;
    }

    public Bundle getBundle() {
        // TODO Auto-generated method stub
        return null;
    }

    public Bundle[] getUsingBundles() {
        // TODO Auto-generated method stub
        return emptyBundleArray;
    }

    public boolean isAssignableTo(Bundle bundle, String className) {
        // TODO Auto-generated method stub
        return false;
    }

    public int compareTo(Object reference) {
        // TODO Auto-generated method stub
        return 0;
    }

}
