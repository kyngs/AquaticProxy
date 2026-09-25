package xyz.kyngs.aquaticproxy.network.session.client;

import net.kyori.adventure.text.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.kyngs.aquaticproxy.api.network.protocol.PacketHandler;
import xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolState;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.configuration.ConfigurationDisconnectPacket;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.login.LoginAcknowledgedPacket;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.login.LoginDisconnectPacket;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.login.LoginStartPacket;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.login.LoginSuccessPacket;
import xyz.kyngs.aquaticproxy.api.network.session.ClientSessionHandler;
import xyz.kyngs.aquaticproxy.api.util.GameProfile;
import xyz.kyngs.aquaticproxy.module.AquaticModuleManager;
import xyz.kyngs.aquaticproxy.network.AquaticClientConnection;

import java.util.List;
import java.util.UUID;

public class LoginSessionHandler implements ClientSessionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginSessionHandler.class);

    private final AquaticClientConnection connection;
    private final AquaticModuleManager moduleManager;
    private final UUID sessionId;
    private GameProfile profile;

    public LoginSessionHandler(AquaticClientConnection connection, AquaticModuleManager moduleManager) {
        super();
        this.connection = connection;
        this.moduleManager = moduleManager;
        this.sessionId = UUID.randomUUID();
    }

    @Override
    public ProtocolState getProtocolState() {
        return ProtocolState.LOGIN;
    }

    @Override
    public void activate() {

    }

    @Override
    public void deactivate() {

    }

    @Override
    public PacketHandler.Result handle(LoginStartPacket packet) {
        profile = new GameProfile(packet.getClaimedUUID(), packet.getUsername(), List.of());
        connection.writePacket(new LoginSuccessPacket(profile, sessionId));
        return PacketHandler.Result.CANCELLED;
    }

    @Override
    public PacketHandler.Result handle(LoginAcknowledgedPacket packet) {
        if (profile == null) {
            LOGGER.warn("Disconnecting client {} because profile is not set", connection);
            connection.disconnect();
            return PacketHandler.Result.CANCELLED;
        }
        var playerModule = moduleManager.getPlayerModule();
        if (playerModule == null) {
            LOGGER.warn("Disconnecting client {} because PlayerModule is not loaded", connection);
            connection.disconnect();
            return PacketHandler.Result.CANCELLED;
        }
        connection.switchProtocolState(new ConfigurationSessionHandler(connection, profile, sessionId, moduleManager, playerModule));
        return PacketHandler.Result.CANCELLED;
    }

    @Override
    public void sendDisconnectReason(Component reason) {
        connection.writePacket(new LoginDisconnectPacket(reason));
    }
}
