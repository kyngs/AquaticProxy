package xyz.kyngs.aquaticproxy.backend;

import xyz.kyngs.aquaticproxy.api.backend.BackendModule;
import xyz.kyngs.aquaticproxy.api.backend.BackendServer;

import java.util.Set;

public class AquaticBackendModule implements BackendModule {
    @Override
    public BackendServer createBackendServer(String id, String host, int port) {
        return null;
    }

    @Override
    public Set<BackendServer> getBackendServers() {
        return Set.of(new AquaticBackendServer("example", "localhost", 25564));
    }

    @Override
    public BackendServer getBackendServer(String id) {
        return null;
    }
}
