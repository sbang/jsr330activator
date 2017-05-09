package no.steria.osgi.jsr330activator;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

/**
 * Defines a single property that will be set when registering a provided service
 * as an OSGi service.
 *
 * The property can either hold a single String value or an array of String values.
 *
 * @author Steinar Bang
 *
 */
@Retention(RUNTIME)
public @interface ServiceProperty {
    String name() default "";
    String value() default "";
    String[] values() default {};
}
