package no.steria.osgi.mocks;

import org.osgi.framework.Bundle;

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
