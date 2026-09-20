package xyz.kyngs.aquaticproxy.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
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
        data.markReaderIndex();
        var packetId = ProtocolUtil.readVarInt(data);

        var handlers = networkModule.getPacketHandlerRegistry().getPublishedServerboundHandlers(sessionHandler.getProtocolState(), protocolVersion.version(), packetId);

        if (handlers != null) {
            for (var handler : handlers) {
                var packet = handler.packet().packetSupplier().get();
                packet.decode(data, protocolVersion);
                switch (handler.handler().handle(packet, this)) {
                    case FORWARD -> {
                    }
                    case MODIFIED -> {
                        data = Unpooled.buffer(data.capacity());
                        packet.encode(data, protocolVersion);
                    }
                    case CANCELLED -> {
                        return;
                    }
                }
            }
        }

        if (pairedConnection == null || pairedConnection.getSessionHandler().getProtocolState() != sessionHandler.getProtocolState()) {
            if (sessionHandler.getProtocolState() == ProtocolState.CONFIGURATION) {
                data.resetReaderIndex();
                queuedConfigurationFrames.add(data);
            }
            return;
        }

        data.resetReaderIndex();
        pairedConnection.writeFrame(ByteBufUtil.getBytes(data));
    }

    @Override
    public @Nullable Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
