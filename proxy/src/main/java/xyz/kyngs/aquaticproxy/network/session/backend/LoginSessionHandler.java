package xyz.kyngs.aquaticproxy.network.session.backend;

import net.kyori.adventure.text.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.kyngs.aquaticproxy.api.network.protocol.PacketHandler;
import xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolState;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.login.*;
import xyz.kyngs.aquaticproxy.api.network.session.BackendSessionHandler;
import xyz.kyngs.aquaticproxy.api.player.Player;
import xyz.kyngs.aquaticproxy.network.AquaticBackendConnection;

public class LoginSessionHandler implements BackendSessionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginSessionHandler.class);

    private final AquaticBackendConnection connection;
    private final Player player;

    public LoginSessionHandler(AquaticBackendConnection connection, Player player) {
        this.connection = connection;
        this.player = player;
    }

    @Override
    public ProtocolState getProtocolState() {
        return ProtocolState.LOGIN;
    }

    @Override
    public PacketHandler.Result handle(EncryptionRequestPacket packet) {
        if (packet.isShouldAuthenticate()) connection.disconnect(Component.text("Backend server is in online-mode."));

        return PacketHandler.Result.CANCELLED;
    }

    @Override
    public PacketHandler.Result handle(LoginDisconnectPacket packet) {
        LOGGER.warn("Backend server disconnected player {} during login: {}", player, packet.getReason());
        connection.disconnect(packet.getReason());

        return PacketHandler.Result.CANCELLED;
    }

    @Override
    public PacketHandler.Result handle(LoginSuccessPacket packet) {
        LOGGER.info("Backend server accepted player {} during login", player);
        connection.switchProtocolState(new ConfigurationSessionHandler(connection, player));

        return PacketHandler.Result.CANCELLED;
    }

    @Override
    public void activate() {
        connection.setProtocolVersion(connection.getPairedConnection().getProtocolVersion());
        connection.writePacket(new LoginStartPacket(player.getId(), player.getUsername()));
    }

    @Override
    public void deactivate() {
        connection.writePacket(new LoginAcknowledgedPacket());
    }
}
