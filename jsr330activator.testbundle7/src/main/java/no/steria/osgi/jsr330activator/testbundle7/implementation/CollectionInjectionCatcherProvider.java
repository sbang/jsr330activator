package no.steria.osgi.jsr330activator.testbundle1.implementation;

import javax.inject.Provider;

import no.steria.osgi.jsr330activator.testbundle1.HelloService;

public class HelloServiceProvider implements Provider<HelloService> {

    public HelloServiceImplementation get() {
        return new HelloServiceImplementation();
    }

}
