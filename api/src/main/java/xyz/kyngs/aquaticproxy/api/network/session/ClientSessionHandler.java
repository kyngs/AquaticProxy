package xyz.kyngs.aquaticproxy.api.network.session;

import net.kyori.adventure.text.Component;
import xyz.kyngs.aquaticproxy.api.network.protocol.PacketHandler;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.configuration.AcknowledgeFinishConfigurationPacket;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.handshake.HandshakePacket;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.login.LoginAcknowledgedPacket;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.login.LoginStartPacket;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.play.AcknowledgeConfigurationPacket;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.status.StatusPingRequestPacket;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.status.StatusRequestPacket;

public interface ClientSessionHandler extends SessionHandler {
    default void sendDisconnectReason(Component reason) {}

    default PacketHandler.Result handle(HandshakePacket packet) { return PacketHandler.Result.FORWARD; }

    default PacketHandler.Result handle(LoginStartPacket packet) { return PacketHandler.Result.FORWARD; }

    default PacketHandler.Result handle(LoginAcknowledgedPacket packet) { return PacketHandler.Result.FORWARD; }

    default PacketHandler.Result handle(StatusPingRequestPacket packet) { return PacketHandler.Result.FORWARD; }

    default PacketHandler.Result handle(StatusRequestPacket packet) { return PacketHandler.Result.FORWARD; }

    default PacketHandler.Result handle(AcknowledgeFinishConfigurationPacket packet) { return PacketHandler.Result.FORWARD; }

    default PacketHandler.Result handle(AcknowledgeConfigurationPacket packet) { return PacketHandler.Result.FORWARD; }
}
