package no.steria.osgi.jsr330activator.testbundle7.implementation;

import java.util.Arrays;
import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import no.steria.osgi.jsr330activator.Optional;
import no.steria.osgi.jsr330activator.testbundle7.NamedServiceOptionalInjectionCatcher;
import no.steria.osgi.jsr330activator.testbundle8.StorageService;

@Named("optionalcatcher")
public class NamedServiceOptionalInjectionCatcherProvider implements Provider<NamedServiceOptionalInjectionCatcher>, NamedServiceOptionalInjectionCatcher {
    @Named("file")
    @Inject
    private StorageService fileStorageService;

    private StorageService databaseStorageService;
    private StorageService dummyStorageService;

    private StorageService nosuchStorageService;

    @Named("database")
    @Inject
    public void setDatabaseStorageService(StorageService service) {
        databaseStorageService = service;
    }

    /**
     * This is an optional injection that will be satisfied in the
     * integration test. The purpose of annotating this injection
     * with {@link Optional}, is to verify that the annotation
     * doesn't affect the regular dependency injection operation.
     *
     * @param service an OSGi injection that will be satisfied.
     */
    @Inject
    @Named("dummy")
    @Optional
    public void setDummyStorageService(StorageService service) {
        dummyStorageService = service;
    }

    /**
     * This is a service that will never be injected, but the
     * provider should still start and register the service.
     *
     * @param service an argument provided for a method never called.
     */
    @Inject
    @Named("nosuchstorageservice")
    @Optional
    public void setNosuchStorageService(StorageService service) {
        nosuchStorageService = service;
    }

    public Collection<String> getInjectedStorageServiceNames() {
        try {
            return Arrays.asList(
                                 getClass().getDeclaredField("fileStorageService").getAnnotation(Named.class).value(),
                                 getClass().getMethod("setDatabaseStorageService", StorageService.class).getAnnotation(Named.class).value(),
                                 getClass().getMethod("setDummyStorageService", StorageService.class).getAnnotation(Named.class).value()
                                 );
        } catch (Exception e) { }

        return Arrays.asList(new String[0]);
    }

    public StorageService getStorageService(String name) {
        try {
            if (getClass().getDeclaredField("fileStorageService").getAnnotation(Named.class).value().equals(name)) {
                return fileStorageService;
            } else if (getClass().getMethod("setDatabaseStorageService", StorageService.class).getAnnotation(Named.class).value().equals(name)) {
                return databaseStorageService;
            } else if (getClass().getMethod("setDummyStorageService", StorageService.class).getAnnotation(Named.class).value().equals(name)) {
                return dummyStorageService;
            }
        } catch (Exception e) { }

        return null;
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
