package xyz.kyngs.aquaticproxy.network.session.backend;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolState;
import xyz.kyngs.aquaticproxy.api.network.session.BackendSessionHandler;
import xyz.kyngs.aquaticproxy.api.player.Player;
import xyz.kyngs.aquaticproxy.network.AquaticBackendConnection;

public class ConfigurationSessionHandler implements BackendSessionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationSessionHandler.class);

    private final AquaticBackendConnection connection;
    private final Player player;

    public ConfigurationSessionHandler(AquaticBackendConnection connection, Player player) {
        this.connection = connection;
        this.player = player;
    }

    @Override
    public ProtocolState getProtocolState() {
        return ProtocolState.CONFIGURATION;
    }

    @Override
    public void activate() {
        for (ByteBuf frame : connection.getPairedConnection().getQueuedConfigurationFrames()) {
            LOGGER.info("Sending queued configuration frame to backend connection for player {}", player);
            connection.writeFrame(ByteBufUtil.getBytes(frame));
        }
    }

    @Override
    public void deactivate() {

    }
}
