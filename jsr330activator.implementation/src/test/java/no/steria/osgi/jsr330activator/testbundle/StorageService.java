package no.steria.osgi.jsr330activator.testbundle;

import java.util.UUID;

public interface StorageService {
    boolean save(UUID id, String data);
    String load(UUID id);
}
