package no.steria.osgi.jsr330activator.testbundle3.implementation;

import javax.inject.Inject;
import javax.inject.Provider;

import org.osgi.framework.BundleContext;

import no.steria.osgi.jsr330activator.ActivatorShutdown;
import no.steria.osgi.jsr330activator.testbundle1.HelloService;
import no.steria.osgi.jsr330activator.testbundle3.HelloService3a;

public class HelloService3aProvider implements Provider<HelloService3a>, HelloService3a {
    private HelloService helloService;
    private String unregistrationMessage;

    @ActivatorShutdown
    public void shuttingDown(BundleContext context) {
    	helloService.unregisterMessage(unregistrationMessage);
    }

    @Inject
    public void setHelloService(HelloService helloService) {
        this.helloService = helloService;
        helloService.registerMessage("I'm here to serve!");
    }

    public String getMessage() {
        return helloService.getMessage();
    }

    public HelloService3a get() {
        return this;
    }

    public void setUnregistrationMessage(String unregistrationMessage) {
        this.unregistrationMessage = unregistrationMessage;
    }

}
