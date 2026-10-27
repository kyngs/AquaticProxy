package xyz.kyngs.aquaticproxy.network.session.client;

import net.kyori.adventure.text.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.kyngs.aquaticproxy.api.backend.BackendConnectionException;
import xyz.kyngs.aquaticproxy.api.event.EventModule;
import xyz.kyngs.aquaticproxy.api.event.backend.ChooseInitialServerEvent;
import xyz.kyngs.aquaticproxy.api.network.protocol.PacketHandler;
import xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolState;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.configuration.AcknowledgeFinishConfigurationPacket;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.configuration.ConfigurationDisconnectPacket;
import xyz.kyngs.aquaticproxy.api.network.session.ClientSessionHandler;
import xyz.kyngs.aquaticproxy.api.player.Player;
import xyz.kyngs.aquaticproxy.api.player.PlayerModule;
import xyz.kyngs.aquaticproxy.api.util.GameProfile;
import xyz.kyngs.aquaticproxy.module.AquaticModuleManager;
import xyz.kyngs.aquaticproxy.network.AquaticClientConnection;

import java.util.UUID;

public class ClientConfigurationSessionHandler implements ClientSessionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientConfigurationSessionHandler.class);

    private final AquaticClientConnection connection;
    private final GameProfile profile;
    private final UUID sessionId;
    private final AquaticModuleManager moduleManager;
    private final EventModule eventModule;
    private final PlayerModule playerModule;
    private Player player;

    public ClientConfigurationSessionHandler(AquaticClientConnection connection, GameProfile profile, UUID sessionId, AquaticModuleManager moduleManager, PlayerModule playerModule) {
        super();
        this.connection = connection;
        this.profile = profile;
        this.sessionId = sessionId;
        this.moduleManager = moduleManager;
        this.eventModule = moduleManager.getEventModule();
        this.playerModule = playerModule;
    }

    @Override
    public ProtocolState getProtocolState() {
        return ProtocolState.CONFIGURATION;
    }

    @Override
    public void activate() {
        if (connection.getPairedConnection() != null) return;

        player = playerModule.registerPlayer(profile, connection);
        connection.setPlayer(player);

        var defaultServer = moduleManager.getBackendModule().getBackendServers().stream().findFirst().orElse(null);

        BackendConnectionException lastException = null;

        for (int i = 0; i < 3; i++) {
            var initialServerEvent = new ChooseInitialServerEvent(player, defaultServer, lastException);
            eventModule.fire(initialServerEvent);

            if (!connection.isConnected()) return;

            var server = initialServerEvent.getServer();
            if (server == null) {
                LOGGER.warn("No initial server chosen for player {}, disconnecting", player);
                player.disconnect(Component.text("There is no server available to connect to."));
                return;
            }

            LOGGER.info("Connecting player {} to initial server {}", player, server);

            try {
                player.connectToBackend(server);
                return;
            } catch (BackendConnectionException e) {
                LOGGER.error("Failed to connect player {} to initial server {}, attempt {}/3", player, server, i + 1, e);
                lastException = e;
            }
        }

        LOGGER.error("Failed to connect player {} to initial server after 3 attempts, disconnecting", player);
        player.disconnect(lastException.getOrGenerateReason());
    }

    @Override
    public void deactivate() {

    }

    @Override
    public PacketHandler.Result handle(AcknowledgeFinishConfigurationPacket packet) {
        switchToPlay();
        return PacketHandler.Result.CANCELLED;
    }

    public void switchToPlay() {
        var pc = connection.getPairedConnection();
        if (pc != null) pc.writePacket(new AcknowledgeFinishConfigurationPacket());
        connection.switchProtocolState(new ClientPlaySessionHandler(connection, player, moduleManager));
    }

    @Override
    public void sendDisconnectReason(Component reason) {
        connection.writePacket(new ConfigurationDisconnectPacket(reason));
    }
}
