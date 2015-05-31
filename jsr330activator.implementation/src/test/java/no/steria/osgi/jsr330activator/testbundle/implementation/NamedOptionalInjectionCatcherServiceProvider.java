package no.steria.osgi.jsr330activator.testbundle.implementation;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import no.steria.osgi.jsr330activator.testbundle.HelloService;
import no.steria.osgi.jsr330activator.testbundle.NamedInjectionCatcherService;

/***
 * A named provider for {@link NamedInjectionCatcherService} for use in unit tests.
 * The provider has two {@link Named} injections of {@link HelloService}.
 * One injection is named "hello1", the other injection is named "hello2".
 *
 * @author Steinar Bang
 *
 */
@Named("hello2")
public class NamedInjectionCatcherServiceProvider implements Provider<NamedInjectionCatcherService>, NamedInjectionCatcherService {

    @Inject
    @Named("hello1")
    private HelloService hello1;
    private HelloService hello2;

    @Named("hello2")
    @Inject
    public void setHello2(HelloService hello2) {
        this.hello2 = hello2;
    }

    public HelloService getHello1() {
        return hello1;
    }

    public HelloService getHello2() {
        return hello2;
    }

    public NamedInjectionCatcherService get() {
        return this;
    }

}
