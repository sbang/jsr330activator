package no.steria.osgi.mocks;

import java.util.Collections;
import java.util.Dictionary;

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
    private Dictionary<String, ?> properties;

    public MockServiceReference(Bundle bundle, Dictionary<String, ?> properties) {
        this.bundle = bundle;
        this.properties = properties;
    }

    @Override
    public Object getProperty(String key) {
    	if (null != properties) {
            return properties.get(key);
    	}

    	return null;
    }

    @Override
    public String[] getPropertyKeys() {
        if (properties != null) {
            return Collections.list(properties.keys()).toArray(new String[1]);
        }

        return new String[0];
    }

    @Override
    public Bundle getBundle() {
        return bundle;
    }

}
