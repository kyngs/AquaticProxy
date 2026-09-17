package xyz.kyngs.aquaticproxy.status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.kyngs.aquaticproxy.api.ResourceOwner;
import xyz.kyngs.aquaticproxy.api.network.protocol.PacketHandler;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.status.StatusPingRequestPacket;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.status.StatusPongResponsePacket;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.status.StatusRequestPacket;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.status.StatusResponsePacket;
import xyz.kyngs.aquaticproxy.network.AquaticClientConnection;
import xyz.kyngs.aquaticproxy.network.protocol.AquaticPacketRegistry;

public class StatusManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatusManager.class);

    public StatusManager(AquaticPacketRegistry registry, ResourceOwner owner) {
        registry.registerServerboundHandler(owner, StatusRequestPacket.class, this::onStatusRequest);
        registry.registerServerboundHandler(owner, StatusPingRequestPacket.class, this::onStatusPing);
    }

    private PacketHandler.Result onStatusPing(StatusPingRequestPacket packet, AquaticClientConnection connection) {
        connection.writePacket(new StatusPongResponsePacket(packet.getTimestamp()));
        return PacketHandler.Result.CANCELLED;
    }

    private PacketHandler.Result onStatusRequest(StatusRequestPacket packet, AquaticClientConnection connection) {
        connection.writePacket(new StatusResponsePacket());
        return PacketHandler.Result.CANCELLED;
    }
}
