package no.steria.osgi.jsr330activator.testbundle.implementation;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import no.steria.osgi.jsr330activator.testbundle.CollectionCatcherService;

public class CollectionCatcherServiceProviderWithNonGenericList implements Provider<CollectionCatcherService>, CollectionCatcherService {
    @SuppressWarnings("rawtypes")
    @Inject
    List storageServices;

    public CollectionCatcherService get() {
        return this;
    }

    public int getNumberOfStorageServices() {
        return storageServices.size();
    }

}
