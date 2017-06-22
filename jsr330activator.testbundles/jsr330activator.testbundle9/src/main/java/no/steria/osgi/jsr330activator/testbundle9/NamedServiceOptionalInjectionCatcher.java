package no.steria.osgi.jsr330activator.testbundle9;

import java.util.Collection;

import no.steria.osgi.jsr330activator.testbundle8.StorageService;

public interface NamedServiceOptionalInjectionCatcher {
    public Collection<String> getInjectedStorageServiceNames();
    public StorageService getStorageService(String name);
    public StorageService getUninjectedOptionalStorageService();
}
