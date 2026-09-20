package xyz.kyngs.aquaticproxy.api.backend;

import net.kyori.adventure.key.Key;
import xyz.kyngs.aquaticproxy.api.module.Module;
import xyz.kyngs.aquaticproxy.api.module.ModuleKey;

import java.util.Map;
import java.util.Set;

public interface BackendModule extends Module {
    ModuleKey<BackendModule> KEY = new ModuleKey<>(Key.key("aquaticproxy", "backend"), BackendModule.class);

    BackendServer createBackendServer(String id, String host, int port);

    Set<BackendServer> getBackendServers();

    BackendServer getBackendServer(String id);
}
