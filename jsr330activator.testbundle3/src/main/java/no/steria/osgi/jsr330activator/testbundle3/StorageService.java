package no.steria.osgi.jsr330activator.testbundle3;

import java.util.UUID;

public interface StorageService {
    public boolean save(UUID id, String data);
    String load(UUID id);
}
