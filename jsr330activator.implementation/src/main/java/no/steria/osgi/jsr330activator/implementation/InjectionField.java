package no.steria.osgi.jsr330activator.implementation;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Named;

import no.steria.osgi.jsr330activator.Optional;

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
        if (fieldIsCollection() &&
            field.getGenericType() instanceof ParameterizedType)
        {
            ParameterizedType fieldTypeAsParameterizedType = (ParameterizedType) field.getGenericType();
            Type[] actualTypeArguments = fieldTypeAsParameterizedType.getActualTypeArguments();
            if (actualTypeArguments.length > 0) {
                Type collectionTypeArgument = actualTypeArguments[0];
                if (collectionTypeArgument instanceof Class<?>) {
                    return (Class<?>) collectionTypeArgument;
                }
            }
        }

        return field.getType();
    }

    public String getNamedValue() {
        Named namedAnnotation = field.getAnnotation(Named.class);
        if (namedAnnotation != null) {
            return namedAnnotation.value();
        }

        return null;
    }

    public boolean isOptional() {
        Optional optionalAnnotation = field.getAnnotation(Optional.class);
        if (optionalAnnotation != null) {
            return true;
        }

        return false;
    }

    @SuppressWarnings("rawtypes")
    public boolean isInjected() {
        if (fieldIsCollection()) {
            Collection fieldAsCollection = getFieldAsCollection();
            return !fieldAsCollection.isEmpty();
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
            Collection fieldAsCollection = getFieldAsCollection();
            fieldAsCollection.remove(service);
            return;
        }

        setInjectedService(null);
    }

    private Object getInjectedService() {
        try {
            return field.get(provider);
        } catch (Exception e) {
            /* Eat exception and continue */
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
            /* Eat exception and continue */
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
    Collection getFieldAsCollection() {
        if (fieldIsCollection()) {
            Collection fieldAsCollection;
            try {
                fieldAsCollection = (Collection) field.get(provider);
                if (fieldAsCollection != null) {
                    return fieldAsCollection;
                }
            } catch (IllegalArgumentException e) {
                /* Eat exception and continue */
            } catch (IllegalAccessException e) {
                /* Eat exception and continue */
            }
        }

        return Collections.emptyList();
    }

    @SuppressWarnings("rawtypes")
    private void createCollectionIfNull() throws IllegalAccessException {
        if (field.get(provider) == null) {
            // If the field has a type that can be instantiated, instantiate
            // that type, using the no-argument constructor.
            if (isInstantiable(field.getType())) {
                try {
                    Object newCollection = field.getType().newInstance();
                    field.set(provider, newCollection);
                    return;
                } catch (IllegalArgumentException e) {
                    /* Eat exception and continue */
                } catch (InstantiationException e) {
                    /* Eat exception and continue */
                }
            }

            // If the field has type Set, use HashSet (services need not implement Comparable)
            if (Set.class.isAssignableFrom(field.getType())) {
                field.set(provider, new HashSet());
            }

            // For everything else (Collection, List etc.), use ArrayList
            field.set(provider, new ArrayList());
        }
    }

    private boolean isInstantiable(Class<?> type) {
        int modifiers = type.getModifiers();
        if (Modifier.isInterface(modifiers)) {
            return false;
        }

        if (Modifier.isAbstract(modifiers)) {
            return false;
        }

        return true;
    }

    private boolean fieldIsCollection() {
        boolean isAssignableFromCollection = Collection.class.isAssignableFrom(field.getType());

        return isAssignableFromCollection;
    }

}
