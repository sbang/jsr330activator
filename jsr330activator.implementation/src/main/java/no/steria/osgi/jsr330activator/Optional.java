package no.steria.osgi.jsr330activator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * Used together with {@link Inject} annotations to tell the
 * {@link Jsr330Activator} that an injection is optional for
 * activating the service of a {@link Provider} as an OSGi
 * service.
 *
 * @author Steinar Bang
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface Optional {

}
