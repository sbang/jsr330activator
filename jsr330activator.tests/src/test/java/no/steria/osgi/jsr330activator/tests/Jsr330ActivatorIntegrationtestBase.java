package no.steria.osgi.jsr330activator.tests;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

class Jsr330ActivatorIntegrationtestBase {

    protected String mavenProjectVersion;

    public Jsr330ActivatorIntegrationtestBase() {
    	try {
            Properties examProperties = new Properties();
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("exam.properties");
            examProperties.load(inputStream);
            mavenProjectVersion = examProperties.getProperty("maven.project.version");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getMavenProjectVersion() {
        return mavenProjectVersion;
    }

}

