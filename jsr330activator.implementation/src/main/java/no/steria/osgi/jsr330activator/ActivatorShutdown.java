package no.steria.osgi.jsr330activator;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Used to signal that the {@link Jsr330Activator} is stopping.
 * Can be used by the service provider to mark a method that will be called
 * when the activator is shutting down.
 *
 * @author Steinar Bang
 *
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface ActivatorShutdown {

}
