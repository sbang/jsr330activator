package no.steria.osgi.jsr330activator.testbundle3.implementation;

import javax.inject.Inject;
import javax.inject.Provider;

import no.steria.osgi.jsr330activator.testbundle1.HelloService;
import no.steria.osgi.jsr330activator.testbundle3.HelloService3a;

public class HelloService3aProvider implements Provider<HelloService3a>, HelloService3a {
    private HelloService helloService;

    @Inject
    public void setHelloService(HelloService helloService) {
        this.helloService = helloService;
    }

    public String getMessage() {
        return helloService.getMessage();
    }

    public HelloService3a get() {
        return this;
    }

}
