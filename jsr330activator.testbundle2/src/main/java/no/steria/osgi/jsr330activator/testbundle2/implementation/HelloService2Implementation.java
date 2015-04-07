package no.steria.osgi.jsr330activator.testbundle2.implementation;

import no.steria.osgi.jsr330activator.testbundle2.HelloService2;

public class HelloService2Implementation implements HelloService2 {

    public String getMessage() {
        return "Hello world2!";
    }

}
