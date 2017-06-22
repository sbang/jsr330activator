package no.steria.osgi.jsr330activator.testbundle9.implementation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import no.steria.osgi.jsr330activator.Optional;
import no.steria.osgi.jsr330activator.testbundle8.StorageService;
import no.steria.osgi.jsr330activator.testbundle9.NamedServiceOptionalInjectionCatcher;

@Named("optionalcatcher")
public class NamedServiceOptionalInjectionCatcherProvider implements Provider<NamedServiceOptionalInjectionCatcher>, NamedServiceOptionalInjectionCatcher {

    private Map<String, StorageService> injectedServices = new HashMap<String, StorageService>();

    private StorageService nosuchStorageService;

    private void registerOrRemoveServiceName(String serviceName, StorageService service) {
        if (service != null) {
            injectedServices.put(serviceName, service);
        } else {
            injectedServices.remove(serviceName);
        }
    }

    @Named("database")
    @Inject
    @Optional
    public void setDatabaseStorageService(StorageService service) {
        registerOrRemoveServiceName("database", service);
    }

    @Inject
    @Named("dummy")
    @Optional
    public void setDummyStorageService(StorageService service) {
        registerOrRemoveServiceName("dummy", service);
    }

    public Collection<String> getInjectedStorageServiceNames() {
        return injectedServices.keySet();
    }

    public StorageService getStorageService(String name) {
        return injectedServices.get(name);
    }

    /**
     * Called from the integration test to verify that the "nosuch"
     * injection hasn't been satisfied, from a service that has
     * been successfully injected into the test (and therefore
     * by definition has been started).
     */
    public StorageService getUninjectedOptionalStorageService() {
        return nosuchStorageService;
    }

    public NamedServiceOptionalInjectionCatcher get() {
        return this;
    }

}
