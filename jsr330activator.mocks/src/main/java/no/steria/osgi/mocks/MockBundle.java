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

    @Override
    public Class<?> loadClass(String classname) throws ClassNotFoundException {
        return getClass().getClassLoader().loadClass(classname);
    }

}
