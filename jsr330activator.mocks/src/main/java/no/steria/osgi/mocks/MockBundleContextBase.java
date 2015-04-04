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
        // TODO Auto-generated method stub
        return null;
    }

    public Bundle getBundle() {
        // TODO Auto-generated method stub
        return null;
    }

    public Bundle installBundle(String location, InputStream input)
        throws BundleException {
        // TODO Auto-generated method stub
        return null;
    }

    public Bundle installBundle(String location) throws BundleException {
        // TODO Auto-generated method stub
        return null;
    }

    public Bundle getBundle(long id) {
        // TODO Auto-generated method stub
        return null;
    }

    public Bundle[] getBundles() {
        // TODO Auto-generated method stub
        return null;
    }

    public void addServiceListener(ServiceListener listener, String filter)
        throws InvalidSyntaxException {
        // TODO Auto-generated method stub

    }

    public void addServiceListener(ServiceListener listener) {
        // TODO Auto-generated method stub

    }

    public void removeServiceListener(ServiceListener listener) {
        // TODO Auto-generated method stub

    }

    public void addBundleListener(BundleListener listener) {
        // TODO Auto-generated method stub

    }

    public void removeBundleListener(BundleListener listener) {
        // TODO Auto-generated method stub

    }

    public void addFrameworkListener(FrameworkListener listener) {
        // TODO Auto-generated method stub

    }

    public void removeFrameworkListener(FrameworkListener listener) {
        // TODO Auto-generated method stub

    }

    public ServiceRegistration<?> registerService(String[] clazzes,
                                                  Object service, Dictionary<String, ?> properties) {
        // TODO Auto-generated method stub
        return null;
    }

    public ServiceRegistration<?> registerService(String clazz, Object service,
                                                  Dictionary<String, ?> properties) {
        // TODO Auto-generated method stub
        return null;
    }

    public <S> ServiceRegistration<S> registerService(Class<S> clazz,
                                                      S service, Dictionary<String, ?> properties) {
        // TODO Auto-generated method stub
        return null;
    }

    public ServiceReference<?>[] getServiceReferences(String clazz,
                                                      String filter) throws InvalidSyntaxException {
        // TODO Auto-generated method stub
        return null;
    }

    public ServiceReference<?>[] getAllServiceReferences(String clazz,
                                                         String filter) throws InvalidSyntaxException {
        // TODO Auto-generated method stub
        return null;
    }

    public ServiceReference<?> getServiceReference(String clazz) {
        // TODO Auto-generated method stub
        return null;
    }

    public <S> ServiceReference<S> getServiceReference(Class<S> clazz) {
        // TODO Auto-generated method stub
        return null;
    }

    public <S> Collection<ServiceReference<S>> getServiceReferences(
                                                                    Class<S> clazz, String filter) throws InvalidSyntaxException {
        // TODO Auto-generated method stub
        return null;
    }

    public <S> S getService(ServiceReference<S> reference) {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean ungetService(ServiceReference<?> reference) {
        // TODO Auto-generated method stub
        return false;
    }

    public File getDataFile(String filename) {
        // TODO Auto-generated method stub
        return null;
    }

    public Filter createFilter(String filter) throws InvalidSyntaxException {
        // TODO Auto-generated method stub
        return null;
    }

    public Bundle getBundle(String location) {
        // TODO Auto-generated method stub
        return null;
    }

}
