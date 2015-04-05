package no.steria.osgi.jsr330activator.testbundle1.implementation;

import no.steria.osgi.jsr330activator.testbundle1.HelloService;

public class HelloServiceImplementation implements HelloService{

    public String getMessage() {
        return "Hello world!";
    }

}
