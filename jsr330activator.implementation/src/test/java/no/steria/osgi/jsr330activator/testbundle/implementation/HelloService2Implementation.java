package no.steria.osgi.jsr330activator.testbundle.implementation;

import no.steria.osgi.jsr330activator.testbundle.HelloService;
import no.steria.osgi.jsr330activator.testbundle.HelloService2;

public class HelloService2Implementation implements HelloService2 {
    HelloService helloService;

    public void setHelloService(HelloService helloService) {
        this.helloService = helloService;
    }

    public String getMessage() {
        return "Hello world2!";
    }

}
