package no.steria.osgi.jsr330activator.testbundle7.implementation;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import no.steria.osgi.jsr330activator.testbundle7.CollectionInjectionCatcher;
import no.steria.osgi.jsr330activator.testbundle8.StorageService;

public class CollectionInjectionCatcherProvider implements Provider<CollectionInjectionCatcher>, CollectionInjectionCatcher {
    @Inject
    List<StorageService> storageServices;

    public CollectionInjectionCatcherProvider get() {
        return this;
    }

    public int getNumberOfInjectedStorageServices() {
        return storageServices.size();
    }

    public Collection<String> getInjectedStorageServiceNames() {
        // TODO Auto-generated method stub
        return null;
    }

    public StorageService getStorageService(String name) {
        // TODO Auto-generated method stub
        return null;
    }

}
