package no.steria.osgi.mocks;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Dictionary;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

/**
 * Default implementations for all methods of {@link BundleContext}.
 *
 * The actual mock class inherits this class, and only needs replace
 * those methods needed by the tests.
 *
 * @author Steinar Bang
 *
 */
public class MockBundleContextBase implements BundleContext {

    public String getProperty(String key) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Bundle getBundle() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Bundle installBundle(String location, InputStream input)
        throws BundleException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Bundle installBundle(String location) throws BundleException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Bundle getBundle(long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Bundle[] getBundles() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void addServiceListener(ServiceListener listener, String filter)
        throws InvalidSyntaxException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void addServiceListener(ServiceListener listener) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void removeServiceListener(ServiceListener listener) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void addBundleListener(BundleListener listener) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void removeBundleListener(BundleListener listener) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void addFrameworkListener(FrameworkListener listener) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void removeFrameworkListener(FrameworkListener listener) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public ServiceRegistration<?> registerService(String[] clazzes,
                                                  Object service, Dictionary<String, ?> properties) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public ServiceRegistration<?> registerService(String clazz, Object service,
                                                  Dictionary<String, ?> properties) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public <S> ServiceRegistration<S> registerService(Class<S> clazz,
                                                      S service, Dictionary<String, ?> properties) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public ServiceReference<?>[] getServiceReferences(String clazz,
                                                      String filter) throws InvalidSyntaxException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public ServiceReference<?>[] getAllServiceReferences(String clazz,
                                                         String filter) throws InvalidSyntaxException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public ServiceReference<?> getServiceReference(String clazz) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public <S> ServiceReference<S> getServiceReference(Class<S> clazz) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public <S> Collection<ServiceReference<S>> getServiceReferences(
                                                                    Class<S> clazz, String filter) throws InvalidSyntaxException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public <S> S getService(ServiceReference<S> reference) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public boolean ungetService(ServiceReference<?> reference) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public File getDataFile(String filename) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Filter createFilter(String filter) throws InvalidSyntaxException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Bundle getBundle(String location) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

}
