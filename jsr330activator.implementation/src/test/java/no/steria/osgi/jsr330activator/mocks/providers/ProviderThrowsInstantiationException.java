package no.steria.osgi.jsr330activator.mocks.providers;

import javax.inject.Provider;

/**
 * A {@link Provider} that throws {@link InstantiationException}
 * because it doesn't have a no-argument constructor.
 *
 * @author Steinar Bang
 *
 */
public class ProviderThrowsInstantiationException implements Provider<String> {

    private String string;

    public ProviderThrowsInstantiationException(String string) {
        super();
        this.string = string;
    }

    public String get() {
        return string;
    }

}
