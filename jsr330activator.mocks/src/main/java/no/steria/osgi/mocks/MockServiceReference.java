package no.steria.osgi.mocks;

import org.osgi.framework.Bundle;

/**
 * A lightweight object holding a reference to a service.
 *
 * <p>This object is usually not created when writing unit tests, it
 * is returned when retrieving a service from {@link MockBundleContext}
 * or returned through a listener object.
 *
 * @author Steinar Bang
 *
 * @param <S> the type of the service being registered.
 */
public class MockServiceReference<S> extends MockServiceReferenceBase<S> {
    Bundle bundle;

    public MockServiceReference(Bundle bundle2) {
        this.bundle = bundle2;
    }

    @Override
    public Bundle getBundle() {
        return bundle;
    }

}
