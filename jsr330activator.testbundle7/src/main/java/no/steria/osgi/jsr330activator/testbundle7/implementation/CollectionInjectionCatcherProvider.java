package no.steria.osgi.jsr330activator.testbundle7.implementation;

import javax.inject.Provider;

import no.steria.osgi.jsr330activator.testbundle7.CollectionInjectionCatcher;

public class CollectionInjectionCatcherProvider implements Provider<CollectionInjectionCatcher>, CollectionInjectionCatcher {

    public String getMessage() {
        // TODO Auto-generated method stub
        return null;
    }

    public CollectionInjectionCatcherProvider get() {
        return this;
    }

}
