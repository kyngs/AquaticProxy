package xyz.kyngs.aquaticproxy.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.kyngs.aquaticproxy.api.network.BackendConnection;
import xyz.kyngs.aquaticproxy.api.network.NetworkFabric;
import xyz.kyngs.aquaticproxy.api.network.protocol.PacketDirection;
import xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolUtil;
import xyz.kyngs.aquaticproxy.api.network.session.BackendSessionHandler;
import xyz.kyngs.aquaticproxy.module.AquaticModuleManager;

public class AquaticBackendConnection extends AquaticConnection<BackendSessionHandler> implements BackendConnection {

    private static final Logger LOGGER = LoggerFactory.getLogger(AquaticBackendConnection.class);

    public AquaticBackendConnection(NetworkFabric.ClientAdapter<?> adapter, AquaticNetworkModule networkManager, AquaticModuleManager moduleManager) {
        super(adapter, networkManager, moduleManager);
    }

    @Override
    protected BackendSessionHandler createInitialSessionHandler() {
        return null;
    }

    @Override
    public void handleFrame(ByteBuf data) {
        var packetId = ProtocolUtil.readVarInt(data);

        var handlers = networkModule.getPacketHandlerRegistry().getPublishedClientboundHandlers(sessionHandler.getProtocolState(), protocolVersion.version(), packetId);

        if (handlers == null || handlers.length == 0) {
            // No handlers registered for this packet, ignore it
            LOGGER.warn("No handlers registered for packet with ID {} in state {} and direction {}", packetId, sessionHandler.getProtocolState(), PacketDirection.CLIENTBOUND);
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
