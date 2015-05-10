package no.steria.osgi.jsr330activator.testbundle4.implementation;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import javax.inject.Named;
import javax.inject.Provider;

import no.steria.osgi.jsr330activator.testbundle8.StorageService;


@Named("database")
public class DatabaseStorageService implements Provider<StorageService>, StorageService {

    private Path databaseDir;

    public DatabaseStorageService() {
    	try {
            databaseDir = Files.createTempDirectory("database");
        } catch (IOException e) {
            databaseDir = null;
        }
    }

    public StorageService get() {
        return this;
    }

    public boolean save(UUID id, String data) {
        String fileName = fullPathFileName(id);
        PrintWriter out = null;
        try {
            out = new PrintWriter(fileName);
            out.print(data);
            return true;
        } catch (FileNotFoundException e) {
            return false;
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    public String load(UUID id) {
        String fileName = fullPathFileName(id);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(fileName));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            return sb.toString();
        } catch (Exception e) {
            return null;
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                return null;
            }
        }
    }

    private String fullPathFileName(UUID id) {
        return databaseDir.resolve(id.toString()).toString();
    }

}
