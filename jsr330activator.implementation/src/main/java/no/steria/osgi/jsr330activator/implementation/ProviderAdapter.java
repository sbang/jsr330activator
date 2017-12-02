package no.steria.osgi.jsr330activator.implementation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import no.steria.osgi.jsr330activator.ActivatorShutdown;
import no.steria.osgi.jsr330activator.Jsr330Activator;
import no.steria.osgi.jsr330activator.Jsr330ActivatorException;
import no.steria.osgi.jsr330activator.ServiceProperties;
import no.steria.osgi.jsr330activator.ServiceProperty;

/**
 * This class defines the operations done by the {@link Jsr330Activator}
 * on the {@link Provider} implementations found inside the bundle.
 *
 * The operations are starting and stopping the provider.
 * Starting the provider means instantiating the provider and causing
 * all injections to be satisfied.
 *
 * @author Steinar Bang
 *
 */
public class ProviderAdapter {

    private static final Class<?>[] emptyArgumentTypes = new Class<?>[0];
    private Object provider;
    private Type providedServiceType;
    private Method activatorShutdownCallback;
    private List<Injection> injections;
    private ServiceRegistration<?> serviceRegistration;

    public ProviderAdapter(Type serviceType, Class<?> providerType) {
        try {
            providedServiceType = serviceType;
            provider = providerType.newInstance();
            activatorShutdownCallback = findActivatorShutdownCallback(provider);
            injections = findInjections(provider);
        } catch (InstantiationException e) {
            throw new Jsr330ActivatorException(e);
        } catch (IllegalAccessException e) {
            throw new Jsr330ActivatorException(e);
        }
    }

    public Type getProvidedServiceType() {
        return providedServiceType;
    }

    public ServiceRegistration<?> getServiceRegistration() {
        return serviceRegistration;
    }

    static Method findActivatorShutdownCallback(Object provider) {
        Class<? extends Object> providerType = provider.getClass();
        Method[] methods = providerType.getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(ActivatorShutdown.class)) {
                return method;
            }
        }

        return null;
    }

    static List<Injection> findInjections(Object provider) {
        List<Injection> injections = new ArrayList<Injection>();
        Class<? extends Object> providerType = provider.getClass();
        Field[] fields = providerType.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Inject.class)) {
                injections.add(new InjectionField(provider, field));
            }
        }

        Method[] methods = providerType.getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(Inject.class) &&
                methodTakesExactlyOneArgument(method))
            {
                // Only interested in methods that take exactly one argument
                injections.add(new InjectionMethod(provider, method));
            }
        }

        return injections;
    }

    private static boolean methodTakesExactlyOneArgument(Method method) {
        return method.getGenericParameterTypes().length == 1;
    }

    public boolean hasInjections() {
        return !injections.isEmpty();
    }

    public void start(BundleContext bundleContext) {
        if (!hasInjections()) {
            // No injections means that the service can be made immediately available
            registerService(bundleContext);
            return;
        }

        setupInjectionListeners(bundleContext);
        checkInjectionsAndRegisterServiceIfSatisfied(bundleContext);
    }

    public void stop(BundleContext context) {
        callActivatorShutdownCallbackIfPresent(context);

        for (Injection injection : injections) {
            injection.unGet(context);
        }

        unregisterMyService();
    }

    private void callActivatorShutdownCallbackIfPresent(BundleContext context) {
        if (activatorShutdownCallback != null) {
            final Object[] args = { context };
            try {
                activatorShutdownCallback.invoke(provider, args);
            } catch (Exception e) {
                // Swallow exceptions quietly.
            }
        }
    }

    private void unregisterMyService() {
        if (!serviceAlreadyUnregistered()) {
            serviceRegistration.unregister();
        }

        // Make sure this now invalid object isn't used again.
        serviceRegistration = null;
    }

    private boolean serviceAlreadyUnregistered() {
        return serviceRegistration == null;
    }

    void registerService(BundleContext bundleContext) {
        try {
            Method getMethod = provider.getClass().getMethod("get", emptyArgumentTypes);
            Object serviceImpl = getMethod.invoke(provider);
            if (providedServiceType instanceof Class) {
                Class<?> service = (Class<?>)providedServiceType;
                String serviceName = service.getCanonicalName();
                Dictionary<String,Object> properties = findServicePropertiesAndServiceName();

                serviceRegistration = bundleContext.registerService(serviceName, serviceImpl, properties);
            }
        } catch (Exception e) {
            /* Eat exception and continue */
        }
    }

    private Dictionary<String, Object> findServicePropertiesAndServiceName() {
        Dictionary<String, Object> properties = findServicePropertyAnnotations();
        Named named = provider.getClass().getAnnotation(Named.class);
        String name = (named!=null) ? named.value() : null;
        if (name != null) {
            properties = (properties!=null) ? properties : new Hashtable<String, Object>();
            properties.put("id", name); // Use "id" as the service property: compatibility with apache aries blueprint blueprint-maven-plugin
        }

        return properties;
    }

    private Dictionary<String, Object> findServicePropertyAnnotations() {
        Dictionary<String, Object> properties = findSingleServiceProperty();
        if (properties != null) {
            // If there is a single property, there can't be multiple properties
            return properties;
        }

        return findMultipleServiceProperties();
    }

    private Dictionary<String, Object> findSingleServiceProperty() {
        ServiceProperty serviceProperty = provider.getClass().getAnnotation(ServiceProperty.class);
        if (serviceProperty == null) {
            return null;
        }

        Hashtable<String, Object> properties = new Hashtable<String, Object>();
        saveServiceProperty(properties, serviceProperty);
        return properties;
    }

    private Dictionary<String, Object> findMultipleServiceProperties() {
        ServiceProperties serviceProperties = provider.getClass().getAnnotation(ServiceProperties.class);
        if (serviceProperties == null || serviceProperties.value().length == 0) {
            return null;
        }

        Hashtable<String, Object> properties = new Hashtable<String, Object>();
        for (ServiceProperty property : serviceProperties.value()) {
            saveServiceProperty(properties, property);
        }

        return properties;
    }

    private void saveServiceProperty(Hashtable<String, Object> properties, ServiceProperty serviceProperty) {
        if (serviceProperty.values().length > 0) {
            properties.put(serviceProperty.name(), serviceProperty.values());
        } else {
            properties.put(serviceProperty.name(), serviceProperty.value());
        }
    }

    void setupInjectionListeners(BundleContext bundleContext) {
        for (Injection injection : injections) {
            injection.setupListener(bundleContext, this);
        }
    }

    void checkInjectionsAndRegisterServiceIfSatisfied(BundleContext bundleContext) {
        if (getServiceRegistration() == null &&
            allInjectionsHaveBeenInjected())
        {
            registerService(bundleContext);
        }
    }

    void checkInjectionsAndUnregisterServiceIfNotSatisfied() {
        // Check for service already unregistered first because that is quickest
        if (!serviceAlreadyUnregistered() && !allInjectionsHaveBeenInjected()) {
            unregisterMyService();
        }
    }

    private boolean allInjectionsHaveBeenInjected() {
        boolean isInjected = true;
        for (Injection injection : injections) {
            isInjected = isInjected && (injection.isInjected() || injection.isOptional());
        }

        return isInjected;
    }
}
