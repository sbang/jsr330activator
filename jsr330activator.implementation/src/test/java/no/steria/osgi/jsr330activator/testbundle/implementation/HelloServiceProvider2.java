package no.steria.osgi.jsr330activator.testbundle.implementation;

import javax.inject.Provider;

import no.steria.osgi.jsr330activator.testbundle.HelloService;

/****
 * Class used to test the reflection code scanning for Provider<> implementations.
 * This class implements multiple interfaces, but the provider interface is first.
 *
 * @author Steinar Bang
 *
 */
public class HelloServiceProvider2 implements Provider<HelloService>, HelloService {

    public String getMessage() {
        return "Hello from HelloServiceProvider2";
    }

    public HelloService get() {
        return this;
    }

}
