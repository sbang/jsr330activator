package no.steria.osgi.jsr330activator.implementation;

import java.lang.reflect.Method;

import javax.inject.Named;

import no.steria.osgi.jsr330activator.Optional;

/**
 * Implementation of {@link Injection} that injects using a method.
 *
 * @author Steinar Bang
 *
 */
class InjectionMethod extends InjectionBase {

    private Object provider;
    private Method method;
    private Object currentService;

    public InjectionMethod(Object provider, Method method) {
        this.provider = provider;
        this.method = method;
        this.method.setAccessible(true);
    }

    public Class<?> getInjectedServiceType() {
        return method.getParameterTypes()[0];
    }

    public String getNamedValue() {
        Named namedAnnotation = method.getAnnotation(Named.class);
        if (namedAnnotation != null) {
            return namedAnnotation.value();
        }

        return null;
    }

    public boolean isOptional() {
        Optional optionalAnnotation = method.getAnnotation(Optional.class);
        if (optionalAnnotation != null) {
            return true;
        }

        return false;
    }

    public boolean isInjected() {
        return currentService != null;
    }

    public void doInject(Object service) {
        try {
            method.invoke(provider, service);
            currentService = service;
        } catch (Exception e) {
        }
    }

    public void doRetract(Object service) {
        try {
            method.invoke(provider, (String)null);
            currentService = null;
        } catch (Exception e) {
        }
    }

}
