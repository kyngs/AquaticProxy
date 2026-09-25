package xyz.kyngs.aquaticproxy.api.network.session;

import xyz.kyngs.aquaticproxy.api.network.protocol.PacketHandler;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.configuration.FinishConfigurationPacket;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.login.EncryptionRequestPacket;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.login.LoginDisconnectPacket;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.login.LoginSuccessPacket;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.play.StartConfigurationPacket;

public interface BackendSessionHandler extends SessionHandler {
    default PacketHandler.Result handle(EncryptionRequestPacket packet) { return PacketHandler.Result.FORWARD; }

    default PacketHandler.Result handle(LoginDisconnectPacket packet) { return PacketHandler.Result.FORWARD; }

    default PacketHandler.Result handle(LoginSuccessPacket packet) { return PacketHandler.Result.FORWARD; }

    default PacketHandler.Result handle(FinishConfigurationPacket packet) { return PacketHandler.Result.FORWARD; }

    default PacketHandler.Result handle(StartConfigurationPacket packet) { return PacketHandler.Result.FORWARD; }
}
