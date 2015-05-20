package no.steria.osgi.jsr330activator.testbundle7.implementation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import no.steria.osgi.jsr330activator.testbundle7.CollectionInjectionCatcher;
import no.steria.osgi.jsr330activator.testbundle8.StorageService;

public class CollectionInjectionCatcherProvider implements Provider<CollectionInjectionCatcher>, CollectionInjectionCatcher {
    @Inject
    List<StorageService> storageServices;

    Map<String, StorageService> serviceNameMap = new HashMap<String, StorageService>();

    public CollectionInjectionCatcherProvider get() {
        return this;
    }

    public int getNumberOfInjectedStorageServices() {
        return storageServices.size();
    }

    public Collection<String> getInjectedStorageServiceNames() {
    	updateServiceNameMapIfNeeded();
        return serviceNameMap.keySet();
    }

    public StorageService getStorageService(String name) {
    	updateServiceNameMapIfNeeded();
        return serviceNameMap.get(name);
    }

    private void updateServiceNameMapIfNeeded() {
        if (serviceNameMap.size() != storageServices.size()) {
            // Size differ: a service has been injected or retracted.
            serviceNameMap.clear();
            for (StorageService storageService : storageServices) {
                String namedValue = getNamedAnnotationValue(storageService);
                serviceNameMap.put(namedValue, storageService);
            }
        }
    }

    private String getNamedAnnotationValue(StorageService storageService) {
    	Annotation[] annotations = storageService.getClass().getAnnotations();
    	for (Annotation annotation : annotations) {
            Class<?>[] interfaces = annotation.getClass().getInterfaces();
            for (Class<?> annotationInterface : interfaces) {
                if (Named.class.getCanonicalName().equals(annotationInterface.getCanonicalName())) {
                    try {
                        Method valueMethod = annotationInterface.getMethod("value", new Class<?>[0]);
                        String namedValue = (String) valueMethod.invoke(annotation, new Object[0]);
                        return namedValue;
                    } catch (Exception e) { }
                }
            }
        }

        return null;
    }

}
