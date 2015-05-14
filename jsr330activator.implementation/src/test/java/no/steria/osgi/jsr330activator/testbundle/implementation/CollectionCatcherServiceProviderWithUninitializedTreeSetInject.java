package no.steria.osgi.jsr330activator.testbundle.implementation;

import java.util.TreeSet;

import javax.inject.Inject;
import javax.inject.Provider;

import no.steria.osgi.jsr330activator.testbundle.CollectionCatcherService;
import no.steria.osgi.jsr330activator.testbundle.StorageService;

public class CollectionCatcherServiceProviderWithUninitializedTreeSetInject implements Provider<CollectionCatcherService>, CollectionCatcherService {
    @Inject
    TreeSet<StorageService> storageServices;

    public CollectionCatcherService get() {
        return this;
    }

    public int getNumberOfStorageServices() {
        return storageServices.size();
    }

}
