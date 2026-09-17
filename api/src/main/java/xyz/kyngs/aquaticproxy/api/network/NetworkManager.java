package xyz.kyngs.aquaticproxy.api.network;

import xyz.kyngs.aquaticproxy.api.network.protocol.PacketRegistry;

import java.net.BindException;

public interface NetworkManager {
    ClientConnection registerClientConnection(NetworkFabric.ClientAdapter<?> adapter);

    PacketRegistry<?, ?> getPacketHandlerRegistry();

    void bind(String host, int port) throws BindException;
}
