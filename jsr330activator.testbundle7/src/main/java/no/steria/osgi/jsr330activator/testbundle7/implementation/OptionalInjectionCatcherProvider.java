package no.steria.osgi.jsr330activator.testbundle7.implementation;

import javax.inject.Provider;

import no.steria.osgi.jsr330activator.testbundle7.OptionalInjectionCatcher;

public class OptionalInjectionCatcherProvider implements Provider<OptionalInjectionCatcher>, OptionalInjectionCatcher {

    public String getMessage() {
        // TODO Auto-generated method stub
        return null;
    }

    public OptionalInjectionCatcher get() {
        return this;
    }

}
