package no.steria.osgi.jsr330activator.testbundle.implementation;

import javax.inject.Provider;

import no.steria.osgi.jsr330activator.testbundle.HelloService2;

/****
 * Class used to test the reflection code scanning for Provider<> implementations.
 * This class implements multiple interfaces, but the provider interface is not the
 * first interface in the interface list.
 *
 * @author Steinar Bang
 *
 */
public class HelloService2Provider2 implements  HelloService2, Provider<HelloService2> {

    public String getMessage() {
        return "Hello from HelloService2Provider2";
    }

    public HelloService2 get() {
        return this;
    }

}
