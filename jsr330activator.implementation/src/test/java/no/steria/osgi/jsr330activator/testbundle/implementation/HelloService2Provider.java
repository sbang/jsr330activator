package no.steria.osgi.jsr330activator.testbundle.implementation;

import javax.inject.Inject;
import javax.inject.Provider;

import no.steria.osgi.jsr330activator.testbundle.HelloService;
import no.steria.osgi.jsr330activator.testbundle.HelloService2;

public class HelloService2Provider implements Provider<HelloService2> {
    @Inject
    private HelloService helloService;

    public HelloService2 get() {
        HelloService2Implementation serviceInstance = new HelloService2Implementation();
        serviceInstance.setHelloService(helloService);
        return serviceInstance;
    }

}
