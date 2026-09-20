package xyz.kyngs.aquaticproxy.api.module;

import xyz.kyngs.aquaticproxy.api.ResourceOwner;
import xyz.kyngs.aquaticproxy.api.backend.BackendModule;
import xyz.kyngs.aquaticproxy.api.event.EventModule;
import xyz.kyngs.aquaticproxy.api.network.NetworkModule;
import xyz.kyngs.aquaticproxy.api.player.PlayerModule;
import xyz.kyngs.aquaticproxy.api.status.ServerStatusModule;

import java.util.List;
import java.util.function.Supplier;

public interface ModuleManager {

    <M extends Module> List<ModuleProvider<M>> getRegisteredProviders(ModuleKey<M> key);

    <M extends Module> ModuleProvider<M> registerProvider(ResourceOwner owner, ModuleKey<M> key, int priority, Supplier<M> provider);

    <M extends Module> M getModule(ModuleKey<M> key);

    default BackendModule getBackendModule() {
        return getModule(BackendModule.KEY);
    }

    default EventModule getEventModule() {
        return getModule(EventModule.KEY);
    }

    default ServerStatusModule getServerStatusModule() {
        return getModule(ServerStatusModule.KEY);
    }

    default NetworkModule getNetworkModule() {
        return getModule(NetworkModule.KEY);
    }

    default PlayerModule getPlayerModule() {
        return getModule(PlayerModule.KEY);
    }
}
