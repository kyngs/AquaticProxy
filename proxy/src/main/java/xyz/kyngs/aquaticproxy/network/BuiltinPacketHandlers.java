package xyz.kyngs.aquaticproxy.network;

import xyz.kyngs.aquaticproxy.api.ResourceOwner;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.handshake.HandshakePacket;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.login.LoginAcknowledgedPacket;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.login.LoginStartPacket;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.status.StatusPingRequestPacket;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.status.StatusRequestPacket;
import xyz.kyngs.aquaticproxy.network.protocol.AquaticPacketRegistry;

public class BuiltinPacketHandlers {
    public static void registerBuiltinPacketHandlers(ResourceOwner owner, AquaticPacketRegistry registry) {
        registry.registerServerboundHandler(owner, HandshakePacket.class, (packet, connection) -> connection.getSessionHandler().handle(packet));
        registry.registerServerboundHandler(owner, LoginStartPacket.class, (packet, connection) -> connection.getSessionHandler().handle(packet));
        registry.registerServerboundHandler(owner, LoginAcknowledgedPacket.class, (packet, connection) -> connection.getSessionHandler().handle(packet));
        registry.registerServerboundHandler(owner, StatusPingRequestPacket.class, (packet, connection) -> connection.getSessionHandler().handle(packet));
        registry.registerServerboundHandler(owner, StatusRequestPacket.class, (packet, connection) -> connection.getSessionHandler().handle(packet));
    }
}
