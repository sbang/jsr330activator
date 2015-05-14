package no.steria.osgi.jsr330activator.testbundle.implementation;

import java.util.LinkedList;
import javax.inject.Inject;
import javax.inject.Provider;

import no.steria.osgi.jsr330activator.testbundle.CollectionCatcherService;
import no.steria.osgi.jsr330activator.testbundle.StorageService;

public class CollectionCatcherServiceProviderWithLinkedList implements Provider<CollectionCatcherService>, CollectionCatcherService {
    @Inject
    LinkedList<StorageService> storageServices;

    public CollectionCatcherService get() {
        return this;
    }

    public int getNumberOfStorageServices() {
        return storageServices.size();
    }

}
