package xyz.kyngs.aquaticproxy.network.session.backend;

import xyz.kyngs.aquaticproxy.api.network.protocol.PacketHandler;
import xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolState;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.configuration.FinishConfigurationPacket;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.play.StartConfigurationPacket;
import xyz.kyngs.aquaticproxy.api.network.session.BackendSessionHandler;
import xyz.kyngs.aquaticproxy.api.player.Player;
import xyz.kyngs.aquaticproxy.network.AquaticBackendConnection;

public class PlaySessionHandler implements BackendSessionHandler {

    private final AquaticBackendConnection connection;
    private final Player player;

    public PlaySessionHandler(AquaticBackendConnection connection, Player player) {
        this.connection = connection;
        this.player = player;
    }

    @Override
    public ProtocolState getProtocolState() {
        return ProtocolState.PLAY;
    }

    @Override
    public void activate() {

    }

    @Override
    public void deactivate() {

    }

    public void switchToConfiguration() {
        var pc = connection.getPairedConnection();
        if (pc != null) pc.writePacket(new StartConfigurationPacket());
        connection.switchProtocolState(new ConfigurationSessionHandler(connection, player));
   }

    @Override
    public PacketHandler.Result handle(StartConfigurationPacket packet) {
        switchToConfiguration();
        return PacketHandler.Result.CANCELLED;
    }
}
