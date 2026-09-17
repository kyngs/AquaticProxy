package xyz.kyngs.aquaticproxy.api.network.session;

import xyz.kyngs.aquaticproxy.api.network.protocol.PacketHandler;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.handshake.HandshakePacket;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.login.LoginAcknowledgedPacket;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.login.LoginStartPacket;

public interface ClientSessionHandler extends SessionHandler {
    default PacketHandler.Result handle(HandshakePacket packet) { return PacketHandler.Result.FORWARD; }

    default PacketHandler.Result handle(LoginStartPacket packet) { return PacketHandler.Result.FORWARD; }

    default PacketHandler.Result handle(LoginAcknowledgedPacket packet) { return PacketHandler.Result.FORWARD; }
}
