package no.steria.osgi.jsr330activator.testbundle3.implementation;

import javax.inject.Inject;
import javax.inject.Provider;

import no.steria.osgi.jsr330activator.testbundle1.HelloService;
import no.steria.osgi.jsr330activator.testbundle2.HelloService2;
import no.steria.osgi.jsr330activator.testbundle3.HelloService3c;

public class HelloService3cProvider implements Provider<HelloService3c>, HelloService3c {
    @Inject
    HelloService helloService;
    HelloService2 helloService2;

    @Inject
    public void setHelloService2(HelloService2 helloService2) {
        this.helloService2 = helloService2;
    }

    public String getCombinedMessage() {
        return helloService.getMessage() + " " + helloService2.getMessage();
    }

    public HelloService3c get() {
        return this;
    }

}
