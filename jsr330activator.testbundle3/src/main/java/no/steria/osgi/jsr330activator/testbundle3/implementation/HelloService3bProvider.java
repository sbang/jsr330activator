package no.steria.osgi.jsr330activator.testbundle3.implementation;

import javax.inject.Inject;
import javax.inject.Provider;

import no.steria.osgi.jsr330activator.testbundle2.HelloService2;
import no.steria.osgi.jsr330activator.testbundle3.HelloService3b;

public class HelloService3bProvider implements Provider<HelloService3b>, HelloService3b {
    @Inject
    private HelloService2 helloService2;

    public String getMessage() {
        return helloService2.getMessage();
    }

    public HelloService3b get() {
        return this;
    }

}
