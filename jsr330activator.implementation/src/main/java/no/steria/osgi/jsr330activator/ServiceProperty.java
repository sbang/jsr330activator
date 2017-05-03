package no.steria.osgi.jsr330activator;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

/**
 * Defines a single property that will be set when registering a provided service
 * as an OSGi service.
 *
 * @author Steinar Bang
 *
 */
@Retention(RUNTIME)
public @interface ServiceProperty {
    String name() default "";
    String value() default "";
}
