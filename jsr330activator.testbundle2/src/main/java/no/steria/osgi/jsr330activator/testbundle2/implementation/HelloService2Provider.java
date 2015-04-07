package no.steria.osgi.jsr330activator.testbundle2.implementation;

import javax.inject.Provider;

import no.steria.osgi.jsr330activator.testbundle2.HelloService2;

public class HelloService2Provider implements Provider<HelloService2> {

    public HelloService2 get() {
        return new HelloService2Implementation();
    }

}
