package no.steria.osgi.jsr330activator.testbundle.implementation;

import java.util.UUID;

import javax.inject.Named;
import javax.inject.Provider;

import no.steria.osgi.jsr330activator.testbundle.StorageService;

@Named("dummy")
public class DummyStorageServiceProvider implements Provider<StorageService>, StorageService {

    public StorageService get() {
        return this;
    }

    public boolean save(UUID id, String data) {
        return false;
    }

    public String load(UUID id) {
        return null;
    }

    public int compareTo(StorageService storageToCompareWith) {
        String myName = ((Named)this.getClass().getAnnotations()[0]).value();
        String nameOfStorageToCompareWith = ((Named)storageToCompareWith.getClass().getAnnotations()[0]).value();
        return myName.compareTo(nameOfStorageToCompareWith);
    }

}
