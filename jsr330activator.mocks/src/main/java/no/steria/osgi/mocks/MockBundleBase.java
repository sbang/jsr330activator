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
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public int getState() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void start(int options) throws BundleException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void start() throws BundleException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void stop(int options) throws BundleException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void stop() throws BundleException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void update(InputStream input) throws BundleException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void update() throws BundleException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void uninstall() throws BundleException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Dictionary<String, String> getHeaders() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public long getBundleId() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public String getLocation() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public ServiceReference<?>[] getRegisteredServices() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public ServiceReference<?>[] getServicesInUse() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public boolean hasPermission(Object permission) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public URL getResource(String name) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Dictionary<String, String> getHeaders(String locale) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public String getSymbolicName() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Class<?> loadClass(String name) throws ClassNotFoundException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Enumeration<URL> getResources(String name) throws IOException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Enumeration<String> getEntryPaths(String path) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public URL getEntry(String path) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public long getLastModified() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Enumeration<URL> findEntries(String path, String filePattern,
                                        boolean recurse) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public BundleContext getBundleContext() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Map<X509Certificate, List<X509Certificate>> getSignerCertificates(
                                                                             int signersType) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Version getVersion() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public <A> A adapt(Class<A> type) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public File getDataFile(String filename) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

}
