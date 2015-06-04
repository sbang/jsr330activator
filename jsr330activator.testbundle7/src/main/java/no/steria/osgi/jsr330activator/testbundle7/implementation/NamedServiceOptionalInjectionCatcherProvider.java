package no.steria.osgi.jsr330activator.testbundle7.implementation;

import java.util.Arrays;
import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import no.steria.osgi.jsr330activator.testbundle7.NamedServiceInjectionCatcher;
import no.steria.osgi.jsr330activator.testbundle8.StorageService;

public class NamedServiceInjectionCatcherProvider implements Provider<NamedServiceInjectionCatcher>, NamedServiceInjectionCatcher {
    @Named("file")
    @Inject
    private StorageService fileStorageService;

    private StorageService databaseStorageService;
    private StorageService dummyStorageService;

    @Named("database")
    @Inject
    public void setDatabaseStorageService(StorageService service) {
        databaseStorageService = service;
    }

    @Inject
    @Named("dummy")
    public void setDummyStorageService(StorageService service) {
        dummyStorageService = service;
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

    public NamedServiceInjectionCatcher get() {
        return this;
    }

}
