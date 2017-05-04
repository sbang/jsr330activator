package no.steria.osgi.jsr330activator.testbundle1.implementation;

import no.steria.osgi.jsr330activator.testbundle1.HelloService;

public class HelloServiceImplementation implements HelloService{
    String message = "Hello world!";

    public String getMessage() {
        return message;
    }

    public void registerMessage(String message) {
        this.message = message;
    }

    public void unregisterMessage(String message) {
    	this.message = message;
    }

}
