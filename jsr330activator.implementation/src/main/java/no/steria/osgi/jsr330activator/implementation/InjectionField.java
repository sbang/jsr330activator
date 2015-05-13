package no.steria.osgi.jsr330activator.implementation;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

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
    	if (fieldIsCollection()) {
            if (field.getGenericType() instanceof ParameterizedType) {
                ParameterizedType fieldTypeAsParameterizedType = (ParameterizedType) field.getGenericType();
                Type[] actualTypeArguments = fieldTypeAsParameterizedType.getActualTypeArguments();
                if (actualTypeArguments.length > 0) {
                    Type collectionTypeArgument = actualTypeArguments[0];
                    if (collectionTypeArgument instanceof Class<?>) {
                        return (Class<?>) collectionTypeArgument;
                    }
                }
            }
    	}

        return field.getType();
    }

    @SuppressWarnings("rawtypes")
    public boolean isInjected() {
    	if (fieldIsCollection()) {
            try {
                Collection fieldAsCollection = getFieldAsCollection();
                return fieldAsCollection.size() > 0;
            } catch (IllegalAccessException e) {
                return false;
            }
    	}

    	Object injectedService = getInjectedService();
        return injectedService != null;
    }

    public void doInject(Object service) {
        setInjectedService(service);
    }

    @SuppressWarnings("rawtypes")
    public void doRetract(Object service) {
    	if (fieldIsCollection()) {
            try {
                Collection fieldAsCollection = getFieldAsCollection();
                fieldAsCollection.remove(service);
                return;
            } catch (IllegalAccessException e) {
            }
    	}

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
            if (fieldIsCollection()) {
                createCollectionIfNull();
                if (addServiceToCollectionField(service)) {
                    return;
                }
            }

            field.set(provider, service);
        } catch (Exception e) {
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private boolean addServiceToCollectionField(Object service) throws IllegalAccessException {
        if (getInjectedServiceType().isAssignableFrom(service.getClass())) {
            Collection fieldAsCollection = getFieldAsCollection();
            fieldAsCollection.add(service);
            return true;
        }

        return false;
    }

    @SuppressWarnings("rawtypes")
    private Collection getFieldAsCollection() throws IllegalAccessException {
        if (fieldIsCollection()) {
            Collection fieldAsCollection = (Collection) field.get(provider);
            if (fieldAsCollection != null) {
                return fieldAsCollection;
            }
        }

        return Collections.emptyList();
    }

    @SuppressWarnings("rawtypes")
    private void createCollectionIfNull() throws IllegalAccessException {
        if (field.get(provider) == null) {
            field.set(provider, new ArrayList());
        }
    }

    private boolean fieldIsCollection() {
        boolean isAssignableFromCollection = Collection.class.isAssignableFrom(field.getType());

        return isAssignableFromCollection;
    }

}
