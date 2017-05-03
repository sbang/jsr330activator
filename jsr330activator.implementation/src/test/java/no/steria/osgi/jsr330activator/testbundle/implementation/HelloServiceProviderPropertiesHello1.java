package no.steria.osgi.jsr330activator.testbundle.implementation;

import javax.inject.Provider;

import no.steria.osgi.jsr330activator.ServiceProperties;
import no.steria.osgi.jsr330activator.ServiceProperty;
import no.steria.osgi.jsr330activator.testbundle.HelloService;

/***
 * A named provider for {@link HelloService} for use in unit tests.
 *
 * @author Steinar Bang
 *
 */
@ServiceProperties({ @ServiceProperty(name = Constants.PROPERTY_NAME, value = "propval"),
                     @ServiceProperty(name = Constants.PROPERTY_NAME2, value = "propval2")})
public class HelloServiceProviderPropertiesHello1 implements Provider<HelloService>, HelloService {

    public String getMessage() {
        return "Hello1 says hi!";
    }

    public HelloService get() {
        return this;
    }

}
