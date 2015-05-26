package no.steria.osgi.mocks;

import java.util.Collections;
import java.util.Dictionary;

import org.osgi.framework.Bundle;

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
