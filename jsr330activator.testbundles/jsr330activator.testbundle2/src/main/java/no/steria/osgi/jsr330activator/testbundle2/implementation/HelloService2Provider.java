package no.steria.osgi.jsr330activator.testbundle2.implementation;

import javax.inject.Provider;

import no.steria.osgi.jsr330activator.ServiceProperties;
import no.steria.osgi.jsr330activator.ServiceProperty;
import no.steria.osgi.jsr330activator.testbundle2.HelloService2;

@ServiceProperties({ @ServiceProperty(name = "someprop", value = "somevalue"),
                     @ServiceProperty(name = "otherprop", value = "othervalue"),
                     @ServiceProperty(name = "lastprop", value = "lastvalue"),
                     @ServiceProperty(name = "multiple", values = { "val1", "val2" }),
                     @ServiceProperty(name = "multiplewithstring", value = "not found", values = {"found1"})})
public class HelloService2Provider implements Provider<HelloService2> {

    public HelloService2 get() {
        return new HelloService2Implementation();
    }

}
