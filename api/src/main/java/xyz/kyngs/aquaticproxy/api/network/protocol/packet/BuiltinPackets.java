package xyz.kyngs.aquaticproxy.api.network.protocol.packet;

import xyz.kyngs.aquaticproxy.api.ResourceOwner;
import xyz.kyngs.aquaticproxy.api.network.protocol.PacketDirection;
import xyz.kyngs.aquaticproxy.api.network.protocol.PacketRegistry;
import xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolState;
import xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolVersion;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.configuration.AcknowledgeFinishConfigurationPacket;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.configuration.FinishConfigurationPacket;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.handshake.HandshakePacket;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.login.*;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.play.AcknowledgeConfigurationPacket;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.play.StartConfigurationPacket;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.status.StatusPingRequestPacket;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.status.StatusPongResponsePacket;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.status.StatusRequestPacket;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.status.StatusResponsePacket;

import static xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolVersion.*;

public class BuiltinPackets {
    public static void registerBuiltinPackets(ResourceOwner owner, PacketRegistry<?, ?> registry) {
        // ==HANDSHAKE==
        registry.buildPacket(owner, HandshakePacket::new)
                .forDirection(PacketDirection.SERVERBOUND)
                .forState(ProtocolState.HANDSHAKE)
                .forId(UNKNOWN, 0x00)
                .register();

        // ==STATUS==

        registry.buildPacket(owner, StatusRequestPacket::new)
                .forDirection(PacketDirection.SERVERBOUND)
                .forState(ProtocolState.STATUS)
                .forId(MINECRAFT_1_7_2, LATEST, 0x00)
                .register();

        registry.buildWriteOnlyPacket(owner, StatusResponsePacket.class)
                .forDirection(PacketDirection.CLIENTBOUND)
                .forState(ProtocolState.STATUS)
                .forId(MINECRAFT_1_7_2, LATEST, 0x00)
                .register();

        registry.buildPacket(owner, StatusPingRequestPacket::new)
                .forDirection(PacketDirection.SERVERBOUND)
                .forState(ProtocolState.STATUS)
                .forId(MINECRAFT_1_7_2, LATEST, 0x01)
                .register();

        registry.buildWriteOnlyPacket(owner, StatusPongResponsePacket.class)
                .forDirection(PacketDirection.CLIENTBOUND)
                .forState(ProtocolState.STATUS)
                .forId(MINECRAFT_1_7_2, LATEST, 0x01)
                .register();

        // ==LOGIN==

        registry.buildPacket(owner, LoginStartPacket::new)
                .forDirection(PacketDirection.SERVERBOUND)
                .forState(ProtocolState.LOGIN)
                .forId(MINECRAFT_1_7_2, LATEST, 0x00)
                .register();

        registry.buildPacket(owner, LoginSuccessPacket::new)
                .forDirection(PacketDirection.CLIENTBOUND)
                .forState(ProtocolState.LOGIN)
                .forId(MINECRAFT_1_7_2, LATEST, 0x02)
                .register();

        registry.buildPacket(owner, LoginAcknowledgedPacket::new)
                .forDirection(PacketDirection.SERVERBOUND)
                .forState(ProtocolState.LOGIN)
                .forId(MINECRAFT_1_20_2, LATEST, 0x03)
                .register();

        registry.buildPacket(owner, EncryptionRequestPacket::new)
                .forDirection(PacketDirection.CLIENTBOUND)
                .forState(ProtocolState.LOGIN)
                .forId(MINECRAFT_1_7_2, LATEST, 0x01)
                .register();

        registry.buildPacket(owner, LoginDisconnectPacket::new)
                .forDirection(PacketDirection.CLIENTBOUND)
                .forState(ProtocolState.LOGIN)
                .forId(MINECRAFT_1_7_2, LATEST, 0x00)
                .register();

        // ==CONFIGURATION==

        registry.buildPacket(owner, () -> AcknowledgeFinishConfigurationPacket.INSTANCE)
                .forDirection(PacketDirection.SERVERBOUND)
                .forState(ProtocolState.CONFIGURATION)
                .forId(MINECRAFT_1_20_2, LATEST, 0x03)
                .register();

        registry.buildPacket(owner, () -> FinishConfigurationPacket.INSTANCE)
                .forDirection(PacketDirection.CLIENTBOUND)
                .forState(ProtocolState.CONFIGURATION)
                .forId(MINECRAFT_1_20_2, LATEST, 0x03)
                .register();

        // ==PLAY==

        registry.buildPacket(owner, () -> StartConfigurationPacket.INSTANCE)
                .forDirection(PacketDirection.CLIENTBOUND)
                .forState(ProtocolState.PLAY)
                .forId(MINECRAFT_1_20_2, LATEST, 0x78)
                .register();

        registry.buildPacket(owner, () -> AcknowledgeConfigurationPacket.INSTANCE)
                .forDirection(PacketDirection.SERVERBOUND)
                .forState(ProtocolState.PLAY)
                .forId(MINECRAFT_1_20_2, LATEST, 0x10)
                .register();
    }
}
