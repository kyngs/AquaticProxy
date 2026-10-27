package xyz.kyngs.aquaticproxy.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.kyngs.aquaticproxy.api.event.EventModule;
import xyz.kyngs.aquaticproxy.api.event.backend.KickedFromServerEvent;
import xyz.kyngs.aquaticproxy.api.network.BackendConnection;
import xyz.kyngs.aquaticproxy.api.network.ClientConnection;
import xyz.kyngs.aquaticproxy.api.network.NetworkFabric;
import xyz.kyngs.aquaticproxy.api.network.protocol.PacketDirection;
import xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolState;
import xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolUtil;
import xyz.kyngs.aquaticproxy.api.network.session.ClientSessionHandler;
import xyz.kyngs.aquaticproxy.api.player.Player;
import xyz.kyngs.aquaticproxy.module.AquaticModuleManager;
import xyz.kyngs.aquaticproxy.network.session.client.HandshakeSessionHandler;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class AquaticClientConnection extends AquaticConnection<ClientSessionHandler, BackendConnection> implements ClientConnection {
    private static final Logger LOGGER = LoggerFactory.getLogger(AquaticClientConnection.class);
    private final Set<ByteBuf> queuedConfigurationFrames;
    private final EventModule eventModule;
    private Player player;

    public AquaticClientConnection(NetworkFabric.ClientAdapter<?> adapter, AquaticNetworkModule networkManager, AquaticModuleManager moduleManager) {
        super(adapter, networkManager, moduleManager);
        this.queuedConfigurationFrames = ConcurrentHashMap.newKeySet();
        this.eventModule = moduleManager.getEventModule();
    }

    @Override
    public Set<ByteBuf> getQueuedConfigurationFrames() {
        return queuedConfigurationFrames;
    }

    @Override
    protected ClientSessionHandler createInitialSessionHandler() {
        return new HandshakeSessionHandler(this, moduleManager);
    }

    @Override
    public void handleFrame(ByteBuf data) {
        data = preProcess(data);
        var packetHeaderIndex = data.readerIndex();

        var packetId = ProtocolUtil.readVarInt(data);
        var state = sessionHandler.getProtocolState();
        var cachedVersion = protocolVersion;

        var handlers = networkModule.getPacketHandlerRegistry().getPublishedServerboundHandlers(state, cachedVersion.version(), packetId);

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

        if (pc == null || pc.getSessionHandler().getProtocolState() != state) {
            if (state == ProtocolState.CONFIGURATION) {
                data.readerIndex(packetHeaderIndex);
                queuedConfigurationFrames.add(data);
            } else data.release();
            return;
        }

        data.readerIndex(packetHeaderIndex);
        if (pc.isConnected()) pc.writeFrame(data);
        data.release();
    }

    @Override
    public void switchPairedConnection(@Nullable BackendConnection connection) {
        if (!connectionLock.writeLock().isHeldByCurrentThread()) {
            throw new IllegalStateException("Cannot switch paired connection without holding the connection's write lock");
        }

        var pc = pairedConnection;
        if (pc != null) {
            pc.disconnect(Component.text("Changing backend connection"));
        }

        setPairedConnection(connection);
    }

    @Override
    public @Nullable Player getPlayer() {
        return player;
    }

    @Override
    public void disconnect(Component reason) {
        getConnectionLock().writeLock().lock();
        try {
            if (!isConnected()) return;
            connected = false;
            try {
                sessionHandler.sendDisconnectReason(reason);
                adapter.close();
            } catch (Exception _) {
            }
            if (pairedConnection != null) {
                pairedConnection.disconnect(reason);
            }
            if (getPlayer() != null) getPlayer().finalizeDisconnection();
        } finally {
            getConnectionLock().writeLock().unlock();
        }
    }

    public void handleServerKick(Component reason, BackendConnection backend) {
        var event = new KickedFromServerEvent(player, new KickedFromServerEvent.Kick(reason), pairedConnection != null && pairedConnection != backend);

        eventModule.fire(event);

        switch (event.getResult()) {
            case KickedFromServerEvent.Kick kick -> player.disconnect(kick.reason());
            case KickedFromServerEvent.Redirect redirect -> player.connectToBackend(redirect.server());
        }
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
