package no.steria.osgi.jsr330activator.testbundle1;

public interface HelloService {
    public String getMessage();
    public void registerMessage(String message);
    public void unregisterMessage(String message);
}
