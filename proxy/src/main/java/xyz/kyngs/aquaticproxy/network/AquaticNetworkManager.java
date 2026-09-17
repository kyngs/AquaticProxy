package xyz.kyngs.aquaticproxy.network;

import xyz.kyngs.aquaticproxy.api.network.ClientConnection;
import xyz.kyngs.aquaticproxy.api.network.NetworkFabric;
import xyz.kyngs.aquaticproxy.api.network.NetworkManager;
import xyz.kyngs.aquaticproxy.network.protocol.AquaticPacketRegistry;
import xyz.kyngs.aquaticproxy.network.server.socket.SocketNetworkFabric;

import java.net.BindException;

public class AquaticNetworkManager implements NetworkManager {

    private final NetworkFabric network;
    private final AquaticPacketRegistry handlerRegistry;

    public AquaticNetworkManager() throws BindException {
        network = new SocketNetworkFabric(this);

        handlerRegistry = new AquaticPacketRegistry();
    }

    @Override
    public ClientConnection registerClientConnection(NetworkFabric.ClientAdapter<?> adapter) {
        return new AquaticClientConnection(adapter, this);
    }

    @Override
    public AquaticPacketRegistry getPacketHandlerRegistry() {
        return handlerRegistry;
    }

    @Override
    public void bind(String host, int port) throws BindException {
        network.bind(host, port);
    }


}
