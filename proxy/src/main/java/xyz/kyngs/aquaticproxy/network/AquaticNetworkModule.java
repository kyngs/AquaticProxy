package xyz.kyngs.aquaticproxy.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.kyngs.aquaticproxy.api.Proxy;
import xyz.kyngs.aquaticproxy.api.backend.BackendServer;
import xyz.kyngs.aquaticproxy.api.network.BackendConnection;
import xyz.kyngs.aquaticproxy.api.network.ClientConnection;
import xyz.kyngs.aquaticproxy.api.network.NetworkFabric;
import xyz.kyngs.aquaticproxy.api.network.NetworkModule;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.BuiltinPackets;
import xyz.kyngs.aquaticproxy.module.AquaticModuleManager;
import xyz.kyngs.aquaticproxy.network.protocol.AquaticPacketRegistry;
import xyz.kyngs.aquaticproxy.network.fabric.SocketNetworkFabric;

import java.io.IOException;
import java.net.BindException;

public class AquaticNetworkModule implements NetworkModule {

    private static final Logger LOGGER = LoggerFactory.getLogger(AquaticNetworkModule.class);

    private final NetworkFabric network;
    private final AquaticPacketRegistry handlerRegistry;
    private final Proxy proxy;
    private final AquaticModuleManager moduleManager;

    public AquaticNetworkModule(Proxy proxy, AquaticModuleManager moduleManager) {
        this.proxy = proxy;
        this.moduleManager = moduleManager;
        network = new SocketNetworkFabric(this);

        handlerRegistry = new AquaticPacketRegistry();
        BuiltinPackets.registerBuiltinPackets(proxy, handlerRegistry);
    }

    @Override
    public ClientConnection registerClientConnection(NetworkFabric.ClientAdapter<?> adapter) {
        return new AquaticClientConnection(adapter, this, moduleManager);
    }

    @Override
    public BackendConnection openBackendConnection(BackendServer server, ClientConnection pairedClient) {
        try {
            return network.openBackendConnection(server, pairedClient);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BackendConnection registerBackendConnection(NetworkFabric.BackendAdapter<?> adapter, ClientConnection pairedClient) {
        return new AquaticBackendConnection(adapter, pairedClient, this, moduleManager);
    }

    @Override
    public AquaticPacketRegistry getPacketHandlerRegistry() {
        return handlerRegistry;
    }

    @Override
    public void bind(String host, int port) throws BindException {
        LOGGER.info("Binding network module to {}:{}", host, port);
        network.bind(host, port);
    }

    @Override
    public void load() {
        BuiltinPacketHandlers.registerBuiltinPacketHandlers(proxy, handlerRegistry);
    }

    @Override
    public void enable() {
        handlerRegistry.publish();
    }

    @Override
    public void disable() {
        try {
            network.close();
        } catch (IllegalStateException ignored) {
        }
    }
}
