package no.steria.osgi.jsr330activator.testbundle7;

import java.util.Collection;

import no.steria.osgi.jsr330activator.testbundle8.StorageService;

public interface NamedServiceInjectionCatcher {
    public Collection<String> getInjectedStorageServiceNames();
    public StorageService getStorageService(String name);
}
