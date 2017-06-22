package no.steria.osgi.jsr330activator;

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

import no.steria.osgi.jsr330activator.implementation.ProviderAdapter;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
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

    private List<ProviderAdapter> serviceProviderAdapters;

    public void start(BundleContext context) throws Exception {
        List<Class<?>> classes = scanBundleForClasses(context.getBundle());
        Map<Type, List<Class<?>>> providers = findProviders(classes);
        serviceProviderAdapters = createProviderAdapterList(providers);
        for (ProviderAdapter serviceProviderAdapter : serviceProviderAdapters) {
            serviceProviderAdapter.start(context);
        }
    }

    public void stop(BundleContext context) throws Exception {
    	for (ProviderAdapter serviceProviderAdapter : serviceProviderAdapters) {
            serviceProviderAdapter.stop(context);
        }
    }

    List<Class<?>> scanBundleForClasses(Bundle bundle) {
    	List<Class<?>> classes = new ArrayList<Class<?>>();
    	try {
            BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);
            if (null != bundleWiring) {
                Collection<String> resources = bundleWiring.listResources("/", "*.class", BundleWiring.LISTRESOURCES_RECURSE | BundleWiring.LISTRESOURCES_LOCAL);
                if (null != resources) {
                    for (String resource : resources) {
                        try {
                            String classname = resource.substring(0, resource.length() - ".class".length()).replaceAll("/", ".");
                            Class<?> clazz = bundle.loadClass(classname);
                            classes.add(clazz);
                        } catch (ClassNotFoundException e) { }
                    }
                }
            }
    	} catch (SecurityException e) { }

        return classes;
    }

    Map<Type, List<Class<?>>> findProviders(List<Class<?>> classesInBundle) {
        Map<Type, List<Class<?>>> providers = new HashMap<Type, List<Class<?>>>();
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
                            putBundleClassInMultimapKeyedOnService(providers, providedService, classInBundle);
                        }
                    }
                }
            }
        }

        return providers;
    }

    private void putBundleClassInMultimapKeyedOnService(Map<Type, List<Class<?>>> providers, Type providedService, Class<?> classInBundle) {
    	if (!providers.containsKey(providedService)) {
    		providers.put(providedService, new ArrayList<Class<?>>());
    	}
    	
    	List<Class<?>> providersForService = providers.get(providedService);
    	providersForService.add(classInBundle);
	}

	public List<ProviderAdapter> createProviderAdapterList(Map<Type, List<Class<?>>> providers) {
        List<ProviderAdapter> providerAdapters = new ArrayList<ProviderAdapter>();
        for (Entry<Type, List<Class<?>>> providerEntry : providers.entrySet()) {
        	for (Class<?> provider : providerEntry.getValue()) {
                ProviderAdapter providerAdapter = new ProviderAdapter(providerEntry.getKey(), provider);
                providerAdapters.add(providerAdapter);
			}
        }

        return providerAdapters;
    }

}
