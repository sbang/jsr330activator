package no.steria.osgi.jsr330activator.testbundle.implementation;

import javax.inject.Named;
import javax.inject.Provider;

import no.steria.osgi.jsr330activator.testbundle.HelloService;

/***
 * A named provider for {@link HelloService} for use in unit tests.
 *
 * @author Steinar Bang
 *
 */
@Named("hello2")
public class HelloServiceProviderNamedHello2 implements Provider<HelloService>, HelloService {

    public String getMessage() {
        return "Hello2 says hi!";
    }

    public HelloService get() {
        return new HelloServiceImplementation();
    }

}
