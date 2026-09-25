package xyz.kyngs.aquaticproxy.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class AquaticClientConnection extends AquaticConnection<ClientSessionHandler, BackendConnection> implements ClientConnection {
    private static final Logger LOGGER = LoggerFactory.getLogger(AquaticClientConnection.class);
    private final Set<ByteBuf> queuedConfigurationFrames;
    private Player player;
    public AquaticClientConnection(NetworkFabric.ClientAdapter<?> adapter, AquaticNetworkModule networkManager, AquaticModuleManager moduleManager) {
        super(adapter, networkManager, moduleManager);
        this.queuedConfigurationFrames = ConcurrentHashMap.newKeySet();
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
        pc.writeFrame(data);
        data.release();
    }

    @Override
    public @Nullable Player getPlayer() {
        return player;
    }

    @Override
    public void disconnect(Component reason) {
        sessionHandler.sendDisconnectReason(reason);
        disconnect();
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
