package xyz.kyngs.aquaticproxy.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.kyngs.aquaticproxy.api.network.ClientConnection;
import xyz.kyngs.aquaticproxy.api.network.NetworkFabric;
import xyz.kyngs.aquaticproxy.api.network.protocol.PacketDirection;
import xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolUtil;
import xyz.kyngs.aquaticproxy.api.network.session.ClientSessionHandler;
import xyz.kyngs.aquaticproxy.module.AquaticModuleManager;
import xyz.kyngs.aquaticproxy.network.session.client.HandshakeSessionHandler;

public class AquaticClientConnection extends AquaticConnection<ClientSessionHandler> implements ClientConnection {
    private static final Logger LOGGER = LoggerFactory.getLogger(AquaticClientConnection.class);

    public AquaticClientConnection(NetworkFabric.ClientAdapter<?> adapter, AquaticNetworkModule networkManager, AquaticModuleManager moduleManager) {
        super(adapter, networkManager, moduleManager);
    }

    @Override
    protected ClientSessionHandler createInitialSessionHandler() {
        return new HandshakeSessionHandler(this, moduleManager);
    }

    @Override
    public void handleFrame(ByteBuf data) {
        var packetId = ProtocolUtil.readVarInt(data);

        var handlers = networkModule.getPacketHandlerRegistry().getPublishedServerboundHandlers(sessionHandler.getProtocolState(), protocolVersion.version(), packetId);

        if (handlers == null || handlers.length == 0) {
            // No handlers registered for this packet, ignore it
            LOGGER.warn("No handlers registered for packet with ID {} in state {} and direction {}", packetId, sessionHandler.getProtocolState(), PacketDirection.SERVERBOUND);
            return;
        }

        for (var handler : handlers) {
            var packet = handler.packet().packetSupplier().get();
            packet.decode(data, protocolVersion);
            switch (handler.handler().handle(packet, this)) {
                case FORWARD -> {}
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
}
