package xyz.kyngs.aquaticproxy.network;

import io.netty.buffer.ByteBuf;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.kyngs.aquaticproxy.api.backend.BackendServer;
import xyz.kyngs.aquaticproxy.api.network.BackendConnection;
import xyz.kyngs.aquaticproxy.api.network.ClientConnection;
import xyz.kyngs.aquaticproxy.api.network.NetworkFabric;
import xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolState;
import xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolUtil;
import xyz.kyngs.aquaticproxy.api.network.session.BackendSessionHandler;
import xyz.kyngs.aquaticproxy.module.AquaticModuleManager;
import xyz.kyngs.aquaticproxy.network.session.backend.HandshakeSessionHandler;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class AquaticBackendConnection extends AquaticConnection<BackendSessionHandler, ClientConnection> implements BackendConnection {

    private static final Logger LOGGER = LoggerFactory.getLogger(AquaticBackendConnection.class);
    private final BackendServer server;
    private final CompletableFuture<ConnectionResult> loginFuture;

    public AquaticBackendConnection(NetworkFabric.BackendAdapter<?> adapter, @Nullable ClientConnection clientConnection, AquaticNetworkModule networkManager, AquaticModuleManager moduleManager) {
        super(adapter, networkManager, moduleManager);
        this.server = adapter.getServer();
        this.loginFuture = new CompletableFuture<>();
        pairedConnection = clientConnection;
        sessionHandler.activate();
    }

    @Override
    protected BackendSessionHandler createInitialSessionHandler() {
        return new HandshakeSessionHandler(this);
    }

    @Override
    public void handleFrame(ByteBuf data) {
        data = preProcess(data);
        var packetHeaderIndex = data.readerIndex();

        var packetId = ProtocolUtil.readVarInt(data);
        var state = sessionHandler.getProtocolState();
        var cachedVersion = protocolVersion;

        var handlers = networkModule.getPacketHandlerRegistry().getPublishedClientboundHandlers(state, cachedVersion.version(), packetId);

        if (handlers != null) {
            var packetDataIndex = data.readerIndex();
            for (var handler : handlers) {
                var packet = handler.packet().packetSupplier().get();
                packet.decode(data, cachedVersion);
                data.readerIndex(packetDataIndex);
                switch (handler.handler().handle(packet, this)) {
                    case FORWARD -> {
                    }
                    case MODIFIED -> {
                        if (data.maxWritableBytes() == 0) {
                            var expanded = data.alloc().buffer(data.writerIndex() * 2);
                            expanded.writeBytes(data, packetHeaderIndex, packetDataIndex);
                            expanded.readerIndex(packetDataIndex);
                            packet.encode(expanded, cachedVersion);
                            data.release();
                            data = expanded;
                        } else {
                            data.writerIndex(packetDataIndex);
                            packet.encode(data, cachedVersion);
                        }
                    }
                    case CANCELLED -> {
                        data.release();
                        return;
                    }
                }
            }
        }

        var pc = pairedConnection;

        if (pc == null || pc.getSessionHandler().getProtocolState() != state) return;
        data.readerIndex(packetHeaderIndex);
        if (pc.isConnected()) pc.writeFrame(data);
        data.release();
    }

    @Override
    public void disconnect(Component reason) {
        getConnectionLock().writeLock().lock();
        try {
            if (!isConnected()) return;
            connected = false;
            try {
                adapter.close();
            } catch (Exception _) {
            }

            var pc = pairedConnection;
            if (pc == null || !getSessionHandler().getProtocolState().isAtLeast(ProtocolState.CONFIGURATION)) return;

            pc.handleServerKick(reason, this);
        } finally {
            getConnectionLock().writeLock().unlock();
        }
    }

    @Override
    public BackendServer getServer() {
        return server;
    }

    @Override
    public CompletableFuture<ConnectionResult> getConnectionFuture() {
        return loginFuture;
    }
}
