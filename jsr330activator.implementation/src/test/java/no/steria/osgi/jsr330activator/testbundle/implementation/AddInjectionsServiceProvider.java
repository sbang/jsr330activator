package no.steria.osgi.jsr330activator.testbundle.implementation;

import javax.inject.Inject;
import javax.inject.Provider;

import no.steria.osgi.jsr330activator.testbundle.AddInjectionsService;

public class AddInjectionsServiceProvider implements Provider<AddInjectionsService>, AddInjectionsService {
    @Inject
    private Integer integerValue;
    private String stringValue;

    @Inject
    void setStringValue(String value) {
        stringValue = value;
    }

    public int addInjections() {
        return integerValue.intValue() + Integer.parseInt(stringValue);
    }

    public AddInjectionsService get() {
        return this;
    }

}
