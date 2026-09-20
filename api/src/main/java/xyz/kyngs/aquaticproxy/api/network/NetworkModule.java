package xyz.kyngs.aquaticproxy.api.network;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.Nullable;
import xyz.kyngs.aquaticproxy.api.backend.BackendServer;
import xyz.kyngs.aquaticproxy.api.module.Module;
import xyz.kyngs.aquaticproxy.api.module.ModuleKey;
import xyz.kyngs.aquaticproxy.api.network.protocol.PacketRegistry;

import java.net.BindException;

public interface NetworkModule extends Module {

    ModuleKey<NetworkModule> KEY = new ModuleKey<>(Key.key("aquaticproxy", "network"), NetworkModule.class);

    ClientConnection registerClientConnection(NetworkFabric.ClientAdapter<?> adapter);

    BackendConnection registerBackendConnection(NetworkFabric.BackendAdapter<?> adapter, @Nullable ClientConnection pairedClient);

    BackendConnection openBackendConnection(BackendServer server, @Nullable ClientConnection pairedClient);

    PacketRegistry<?, ?> getPacketHandlerRegistry();

    void bind(String host, int port) throws BindException;
}
