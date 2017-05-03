package no.steria.osgi.jsr330activator;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;


/**
 * Defines list of properties that will be set when registering a provided service
 * as an OSGi service.
 *
 * @author Steinar Bang
 *
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface ServiceProperties {

    public ServiceProperty[] value() default {};
}
