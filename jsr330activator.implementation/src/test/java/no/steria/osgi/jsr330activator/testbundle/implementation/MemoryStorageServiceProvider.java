package no.steria.osgi.jsr330activator.testbundle.implementation;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.inject.Named;
import javax.inject.Provider;

import no.steria.osgi.jsr330activator.testbundle.StorageService;

@Named("memory")
public class MemoryStorageServiceProvider implements Provider<StorageService>, StorageService {
    Map<UUID, String> store = new HashMap<UUID, String>();

    public StorageService get() {
        return this;
    }

    public boolean save(UUID id, String data) {
        store.put(id, data);
        return true;
    }

    public String load(UUID id) {
        return store.get(id);
    }

    public int compareTo(StorageService storageToCompareWith) {
        String myName = ((Named)this.getClass().getAnnotations()[0]).value();
        String nameOfStorageToCompareWith = ((Named)storageToCompareWith.getClass().getAnnotations()[0]).value();
        return myName.compareTo(nameOfStorageToCompareWith);
    }

}
