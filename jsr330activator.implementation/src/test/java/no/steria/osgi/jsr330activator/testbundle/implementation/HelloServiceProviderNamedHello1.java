package no.steria.osgi.jsr330activator.testbundle.implementation;

import javax.inject.Provider;

import no.steria.osgi.jsr330activator.testbundle.HelloService;

public class HelloServiceProvider implements Provider<HelloService> {

    public HelloService get() {
        return new HelloServiceImplementation();
    }

}
