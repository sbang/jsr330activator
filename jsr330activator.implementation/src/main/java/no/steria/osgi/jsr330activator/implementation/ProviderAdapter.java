package no.steria.osgi.jsr330activator.implementation;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

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
    private List<Injection> injections;
    private ServiceRegistration<?> serviceRegistration;

    public ProviderAdapter(Type serviceType, Class<?> providerType) {
        try {
            providedServiceType = serviceType;
            provider = providerType.newInstance();
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
        for (Injection injection : injections) {
            injection.unGet(context);
        }

        serviceRegistration.unregister();
    }

    void registerService(BundleContext bundleContext) {
        try {
            Method getMethod = provider.getClass().getMethod("get", emptyArgumentTypes);
            Object serviceImpl = getMethod.invoke(provider);
            if (providedServiceType instanceof Class) {
                Class<?> service = (Class<?>)providedServiceType;
                String serviceName = service.getCanonicalName();
                serviceRegistration = bundleContext.registerService(serviceName, serviceImpl, null);
            }
        } catch (IllegalAccessException e) {
        } catch (NoSuchMethodException e) {
        } catch (SecurityException e) {
        } catch (IllegalArgumentException e) {
        } catch (InvocationTargetException e) {
        }
    }

    void setupInjectionListeners(BundleContext bundleContext) {
        for (Injection injection : injections) {
            injection.setupListener(bundleContext, this);
        }
    }

    void checkInjectionsAndRegisterServiceIfSatisfied(BundleContext bundleContext) {
    	if (allInjectionsHaveBeenInjected()) {
            registerService(bundleContext);
    	}
    }

    void checkInjectionsAndUnregisterServiceIfNotSatisfied(BundleContext bundleContext) {
    	if (null != serviceRegistration && !allInjectionsHaveBeenInjected()) {
            serviceRegistration.unregister();
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
