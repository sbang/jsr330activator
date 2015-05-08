package no.steria.osgi.jsr330activator.testbundle6.implementation;

import java.util.UUID;

import javax.inject.Named;
import javax.inject.Provider;

import no.steria.osgi.jsr330activator.testbundle3.StorageService;

@Named("dummy")
public class DummyStorageService implements Provider<StorageService>, StorageService {

    public StorageService get() {
        return this;
    }

    public boolean save(UUID id, String data) {
    	return false;
    }

    public String load(UUID id) {
    	return "";
    }

}
