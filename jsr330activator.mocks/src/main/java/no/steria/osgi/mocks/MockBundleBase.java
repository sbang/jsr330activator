package no.steria.osgi.mocks;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;

/**
 * Default implementations for all methods of {@link Bundle}.
 *
 * The actual mock class inherits this class, and only needs replace
 * those methods needed by the tests.
 *
 * @author Steinar Bang
 *
 */
public class MockBundleBase implements Bundle {

    public int compareTo(Bundle arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getState() {
        // TODO Auto-generated method stub
        return 0;
    }

    public void start(int options) throws BundleException {
        // TODO Auto-generated method stub

    }

    public void start() throws BundleException {
        // TODO Auto-generated method stub

    }

    public void stop(int options) throws BundleException {
        // TODO Auto-generated method stub

    }

    public void stop() throws BundleException {
        // TODO Auto-generated method stub

    }

    public void update(InputStream input) throws BundleException {
        // TODO Auto-generated method stub

    }

    public void update() throws BundleException {
        // TODO Auto-generated method stub

    }

    public void uninstall() throws BundleException {
        // TODO Auto-generated method stub

    }

    public Dictionary<String, String> getHeaders() {
        // TODO Auto-generated method stub
        return null;
    }

    public long getBundleId() {
        // TODO Auto-generated method stub
        return 0;
    }

    public String getLocation() {
        // TODO Auto-generated method stub
        return null;
    }

    public ServiceReference<?>[] getRegisteredServices() {
        // TODO Auto-generated method stub
        return null;
    }

    public ServiceReference<?>[] getServicesInUse() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean hasPermission(Object permission) {
        // TODO Auto-generated method stub
        return false;
    }

    public URL getResource(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    public Dictionary<String, String> getHeaders(String locale) {
        // TODO Auto-generated method stub
        return null;
    }

    public String getSymbolicName() {
        // TODO Auto-generated method stub
        return null;
    }

    public Class<?> loadClass(String name) throws ClassNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    public Enumeration<URL> getResources(String name) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    public Enumeration<String> getEntryPaths(String path) {
        // TODO Auto-generated method stub
        return null;
    }

    public URL getEntry(String path) {
        // TODO Auto-generated method stub
        return null;
    }

    public long getLastModified() {
        // TODO Auto-generated method stub
        return 0;
    }

    public Enumeration<URL> findEntries(String path, String filePattern,
                                        boolean recurse) {
        // TODO Auto-generated method stub
        return null;
    }

    public BundleContext getBundleContext() {
        // TODO Auto-generated method stub
        return null;
    }

    public Map<X509Certificate, List<X509Certificate>> getSignerCertificates(
                                                                             int signersType) {
        // TODO Auto-generated method stub
        return null;
    }

    public Version getVersion() {
        // TODO Auto-generated method stub
        return null;
    }

    public <A> A adapt(Class<A> type) {
        // TODO Auto-generated method stub
        return null;
    }

    public File getDataFile(String filename) {
        // TODO Auto-generated method stub
        return null;
    }

}
