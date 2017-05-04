package no.steria.osgi.jsr330activator.testbundle1.implementation;

import javax.inject.Provider;

import no.steria.osgi.jsr330activator.ServiceProperty;
import no.steria.osgi.jsr330activator.testbundle1.HelloService;

@ServiceProperty(name = "someprop", value = "somevalue")
public class HelloServiceProvider implements Provider<HelloService> {

    public HelloServiceImplementation get() {
        return new HelloServiceImplementation();
    }

}
