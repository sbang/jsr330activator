package no.steria.osgi.jsr330activator.implementation;

import java.lang.reflect.Field;

/**
 * An {@link Injection} implementation that operates on
 * a provider field.  The field may be declared as private.
 *
 * @author Steinar Bang
 *
 */
class InjectionField extends InjectionBase {

    private Object provider;
    private Field field;

    public InjectionField(Object provider, Field field) {
        this.provider = provider;
        this.field = field;
        this.field.setAccessible(true);
    }

    public Class<?> getInjectedServiceType() {
        return field.getType();
    }

    public boolean isInjected() {
        Object injectedService = getInjectedService();
        return injectedService != null;
    }

    public void doInject(Object service) {
        setInjectedService(service);
    }

    public void doRetract() {
        setInjectedService(null);
    }

    private Object getInjectedService() {
        try {
            return field.get(provider);
        } catch (Exception e) {
        }

        return null;
    }

    private void setInjectedService(Object service) {
        try {
            field.set(provider, service);
        } catch (Exception e) {
        }
    }

}
