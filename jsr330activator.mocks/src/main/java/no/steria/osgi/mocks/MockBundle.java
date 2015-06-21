package no.steria.osgi.mocks;

import org.osgi.framework.wiring.BundleWiring;

/**
 * Mock implementation of {@link Bundle}.
 *
 * @author Steinar Bang
 *
 */
public class MockBundle extends MockBundleBase {
    private BundleWiring wiring;

    /**
     * A constructor that takes a {@link BundleWiring} argument.
     * This OSGi mock package doesn't have an implementation of
     * BundleWiring, so if one is needed one must use mockito
     * or another mock framework to create it.
     *
     * @param wiring a {@link BundleWiring} instance used for class conversion
     */
    public MockBundle(BundleWiring wiring) {
        this.wiring = wiring;
    }

    public MockBundle() {
        this.wiring = null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <A> A adapt(Class<A> type) {
        if (BundleWiring.class.equals(type)) {
            return (A) wiring;
        }

        return null;
    }

    /**
     * Get the class loader of the mock, rather than that of the bundle.
     */
    @Override
    public Class<?> loadClass(String classname) throws ClassNotFoundException {
        return getClass().getClassLoader().loadClass(classname);
    }

}
