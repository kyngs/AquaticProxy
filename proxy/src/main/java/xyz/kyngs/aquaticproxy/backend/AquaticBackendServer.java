package xyz.kyngs.aquaticproxy.backend;

import xyz.kyngs.aquaticproxy.api.backend.BackendServer;

public record AquaticBackendServer(String id, String host, int port) implements BackendServer {
    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public int getPort() {
        return port;
    }
}
