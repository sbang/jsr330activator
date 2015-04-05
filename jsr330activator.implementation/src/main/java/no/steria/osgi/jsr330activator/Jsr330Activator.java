package no.steria.osgi.jsr330activator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.inject.Provider;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.wiring.BundleWiring;

/**
 * An OSGi {@link BundleActivator} implementation, that will scan the
 * current bundle's class path for JSR330 {@link Inject} annotations
 * and {@link Provider} implementations.
 *
 * On {@link #start(BundleContext)}, this activator will then set up listeners
 * for all injected services and register services for all providers that don't
 * require injections.
 *
 * When providers requiring injections, receive all of their required injections,
 * they will be used to create service implementations that will be registered
 * with the bundle context.
 *
 * When required injections are removed from the OSGi service registry,
 * the services requiring them will be unregistered.
 *
 * On {@link #stop(BundleContext)}, this activator will unregister
 * all services still running.
 *
 * @author Steinar Bang
 *
 */
public class Jsr330Activator implements BundleActivator {

    private static final Class<?>[] emptyArgumentTypes = new Class<?>[0];
    private static final Object[] emptyArgumentList = new Object[0];
    private List<ServiceRegistration<?>> serviceRegistrations = new ArrayList<ServiceRegistration<?>>();

    public void start(BundleContext context) throws Exception {
        List<Class<?>> classes = scanBundleForClasses(context.getBundle());
        Map<Type, Class<?>> providers = findProviders(classes);
        registerServices(context, providers);
    }

    public void stop(BundleContext context) throws Exception {
        for (ServiceRegistration<?> serviceRegistration : serviceRegistrations) {
            serviceRegistration.unregister();
        }
    }

    List<Class<?>> scanBundleForClasses(Bundle bundle) {
    	List<Class<?>> classes = new ArrayList<Class<?>>();
    	try {
            BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);
            if (null != bundleWiring) {
                Collection<String> classnames = bundleWiring.listResources("/", "*", BundleWiring.LISTRESOURCES_LOCAL);
                if (null != classnames) {
                    ClassLoader bundleClassLoader = bundleWiring.getClassLoader();
                    if (null != bundleClassLoader) {
                    	for (String classname : classnames) {
                            try {
                                Class<?> clazz = bundleClassLoader.loadClass(classname);
                                classes.add(clazz);
                            } catch (ClassNotFoundException e) { }
                        }
                    }
                }
            }
    	} catch (SecurityException e) { }

        return classes;
    }

    Map<Type, Class<?>> findProviders(List<Class<?>> classesInBundle) {
        Map<Type, Class<?>> providers = new HashMap<Type, Class<?>>();
        for (Class<?> classInBundle : classesInBundle) {
            Type[] genericInterfaces = classInBundle.getGenericInterfaces();
            if (genericInterfaces.length > 0) {
                Type genericInterface = genericInterfaces[0];
                if (genericInterface instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) genericInterface;
                    Class<?> rawType = (Class<?>) parameterizedType.getRawType();
                    if (Provider.class.equals(rawType)) {
                        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                        if (actualTypeArguments.length > 0) {
                            Type providedService = actualTypeArguments[0];
                            providers.put(providedService, classInBundle);
                        }
                    }
                }
            }
        }

        return providers;
    }

    void registerServices(BundleContext bundleContext, Map<Type, Class<?>> serviceImplementations) {
        for (Entry<Type, Class<?>> serviceImplementation : serviceImplementations.entrySet()) {
            try {
                Object provider = serviceImplementation.getValue().newInstance();
                Method getMethod = serviceImplementation.getValue().getMethod("get", emptyArgumentTypes);
                Object serviceImpl = getMethod.invoke(provider, emptyArgumentList);
                if (serviceImplementation.getKey() instanceof Class) {
                    Class<?> service = (Class<?>)serviceImplementation.getKey();
                    String serviceName = service.getCanonicalName();
                    ServiceRegistration<?> serviceRegistration = bundleContext.registerService(serviceName, serviceImpl, null);
                    serviceRegistrations.add(serviceRegistration);
                }
            } catch (InstantiationException e) {
            } catch (IllegalAccessException e) {
            } catch (NoSuchMethodException e) {
            } catch (SecurityException e) {
            } catch (IllegalArgumentException e) {
            } catch (InvocationTargetException e) {
            }
        }
    }

}
