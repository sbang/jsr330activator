package no.steria.osgi.jsr330activator.testbundle.implementation;

import no.steria.osgi.jsr330activator.testbundle.HelloService;

public class HelloServiceImplementation implements HelloService{

    public String getMessage() {
        return "Hello world!";
    }

}
