package no.steria.osgi.jsr330activator.mocks.providers;

import javax.inject.Provider;

/**
 * A {@link Provider} that throws {@link IllegalAccessException}
 * because its constructor is private.
 *
 * @author Steinar Bang
 *
 */
public class ProviderThrowsIllegalAccessException implements Provider<String> {

    private String string;

    private ProviderThrowsIllegalAccessException() {
        string = "Hello there!";
    }

    public String get() {
        return string;
    }

}
