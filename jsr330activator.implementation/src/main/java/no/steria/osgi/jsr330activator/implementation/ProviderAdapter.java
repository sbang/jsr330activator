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
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
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
            if (method.isAnnotationPresent(Inject.class)) {
                // Only interested in methods that take exactly one argument
                if (method.getGenericParameterTypes().length == 1) {
                    injections.add(new InjectionMethod(provider, method));
                }
            }
        }

        return injections;
    }

    public boolean hasInjections() {
        return injections.size() > 0;
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
            Named named = provider.getClass().getAnnotation(Named.class);
            String name = (named!=null) ? named.value() : null;
            Object serviceImpl = getMethod.invoke(provider);
            if (providedServiceType instanceof Class) {
                Class<?> service = (Class<?>)providedServiceType;
                String serviceName = service.getCanonicalName();
                Dictionary<String, String> properties = (name!=null) ? new Hashtable<String, String>() : null;
                if (properties != null) {
                    properties.put("id", name); // Use "id" as the service property: compatibility with apache aries blueprint blueprint-maven-plugin
                }

                serviceRegistration = bundleContext.registerService(serviceName, serviceImpl, properties);
            }
        } catch (Exception e) {
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

    void checkInjectionsAndUnregisterServiceIfNotSatisfied(BundleContext bundleContext) {
    	// Check for service already unregistered first because that is quickest
    	if (!serviceAlreadyUnregistered() && !allInjectionsHaveBeenInjected()) {
            unregisterMyService();
    	}
    }

    private boolean allInjectionsHaveBeenInjected() {
        boolean isInjected = true;
        for (Injection injection : injections) {
            isInjected = isInjected & injection.isInjected();
        }

        return isInjected;
    }
}
