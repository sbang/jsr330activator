package no.steria.osgi.jsr330activator.testbundle7.implementation;

import javax.inject.Provider;

import no.steria.osgi.jsr330activator.testbundle7.NamedServiceInjectionCatcher;

public class NamedServiceInjectionCatcherProvider implements Provider<NamedServiceInjectionCatcher>, NamedServiceInjectionCatcher {

    public String getMessage() {
        // TODO Auto-generated method stub
        return null;
    }

    public NamedServiceInjectionCatcher get() {
        return this;
    }

}
