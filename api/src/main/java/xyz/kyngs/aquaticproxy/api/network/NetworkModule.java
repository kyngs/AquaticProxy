package xyz.kyngs.aquaticproxy.api.network;

import net.kyori.adventure.key.Key;
import xyz.kyngs.aquaticproxy.api.module.Module;
import xyz.kyngs.aquaticproxy.api.module.ModuleKey;
import xyz.kyngs.aquaticproxy.api.network.protocol.PacketRegistry;

import java.net.BindException;

public interface NetworkModule extends Module {

    ModuleKey<NetworkModule> KEY = new ModuleKey<>(Key.key("aquaticproxy", "network"), NetworkModule.class);

    ClientConnection registerClientConnection(NetworkFabric.ClientAdapter<?> adapter);

    PacketRegistry<?, ?> getPacketHandlerRegistry();

    void bind(String host, int port) throws BindException;
}
